package com.matthiasrothe.emerald.termux.dl4j.recorder.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.UUID;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.matthiasrothe.emerald.termux.bindings.annotations.TimedSensorReadingQueueBinding;
import com.matthiasrothe.emerald.termux.model.Status;
import com.matthiasrothe.emerald.termux.model.TimedSensorReading;
import com.matthiasrothe.emerald.termux.thread.AbortablePaecoThread;

import net.sf.jetro.stream.visitor.LazilyParsedNumber;
import net.sf.jetro.tree.JsonArray;
import net.sf.jetro.tree.JsonNumber;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.JsonProperty;
import net.sf.jetro.tree.JsonString;
import net.sf.jetro.tree.builder.JsonTreeBuilder;

@Singleton
public class RecorderClient extends AbortablePaecoThread {
	private static final int BATCH_SIZE = 30;
	
	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	
	private Throwable throwable;
	
	private final List<TimedSensorReading> sensorReadings = new ArrayList<>();
	private final JsonTreeBuilder builder = new JsonTreeBuilder();
	
	@Inject
	@TimedSensorReadingQueueBinding
	private PriorityQueue<TimedSensorReading> sensorReadingQueue;
	
	@Inject
	private Status status;
	
	@Override
	protected void prepare() {
		if (socket == null) {
			try (BufferedReader configReader = new BufferedReader(new FileReader(
					Paths.get("./emerald-termux-config.json").toFile()))) {
				JsonObject config = (JsonObject) builder.build(configReader);
				JsonObject recorderClientConfig = (JsonObject) config.get("recorderClientConfig");
				
				String host = ((JsonString) recorderClientConfig.get("host")).getValue();
				int port = ((LazilyParsedNumber) ((JsonNumber) recorderClientConfig.get("port"))
						.getValue()).intValue();
				
				socket = new Socket(host, port);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				
				status.setRecorderClientStatus("Initialized");

				synchronized (sensorReadingQueue) {
					sensorReadingQueue.wait();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throwable = e;
			}
		}
	}

	@Override
	protected void act() {
		int count = 0;
		
		while (throwable == null && !sensorReadingQueueIsEmpty() && count++ < BATCH_SIZE) {
			TimedSensorReading sensorReading = null; 
			
			synchronized (sensorReadingQueue) {
				sensorReading = sensorReadingQueue.poll();
			}
			
			if (sensorReading != null) {
				sensorReadings.add(sensorReading);
			}
			
			if (sensorReadings.size() == BATCH_SIZE) {
				String uuid = UUID.randomUUID().toString();
				JsonObject message = prepareMessage(uuid);
				
				try {
					String response = sendMessage(message);
					checkResponse(response, uuid);
					sensorReadings.clear();
					status.addDataPointsSuccessfullySent(BATCH_SIZE);
				} catch (Exception e) {
					throwable = e;
				}
			}
		}
	}
	
	private boolean sensorReadingQueueIsEmpty() {
		synchronized (sensorReadingQueue) {
			return sensorReadingQueue.isEmpty();
		}
	}
	
	private JsonObject prepareMessage(final String uuid) {
		JsonObject header = new JsonObject();
		header.add(new JsonProperty("type", "batch"));
		header.add(new JsonProperty("uuid", uuid));
		
		JsonArray body = new JsonArray();	
		for (TimedSensorReading sensorReading : sensorReadings) {
			JsonArray values = new JsonArray();
			for (Double d : sensorReading.getValues()) {
				values.add(new JsonNumber(d));
			}
			
			JsonObject content = new JsonObject();
			content.add(new JsonProperty("sensor", sensorReading.getSensorName()));
			content.add(new JsonProperty("timestamp", sensorReading.getTimestamp().toString()));
			content.add(new JsonProperty("values", values));
			
			body.add(content);
		}
		
		JsonObject message = new JsonObject();
		message.add(new JsonProperty("header", header));
		message.add(new JsonProperty("body", body));
		
		return message;
	}
	
	private String sendMessage(final JsonObject message) throws IOException {
		out.write(message.toJson());
		out.newLine();
		out.flush();
		
		return in.readLine();
	}
	
	private void checkResponse(final String response, final String uuid) {
		if (!("{\"status\":\"OK\",\"statusCode\":200,\"uuid\":\"" + uuid + "\"}").equals(response)) {
			throw new RuntimeException("Received response: " + response);
		}
	}
	
	@Override
	protected void evaluate() {
		if (throwable == null) {
			status.setRecorderClientStatus("Running");
		} else {
			status.setRecorderClientStatus("An error occurred: " + throwable.getClass().getName()
					+ " [" + throwable.getMessage() + "]");
		}
	}

	@Override
	protected void consolidate() {
		if (throwable != null) {
			abort();
		}
	}

	@Override
	protected void optimize() {
		if (shouldAbort() && socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (socket != null) {
			waitForSensorReadingQueue();
		}
	}
	
	@Override
	protected void beforePause() {
		status.setRecorderClientStatus("Paused");
		waitForSensorReadingQueue();
	}

	@Override
	protected void afterPause() {
		status.setRecorderClientStatus("Running");
	}
	
	private void waitForSensorReadingQueue() {
		synchronized (sensorReadingQueue) {
			try {
				sensorReadingQueue.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
	}
}
