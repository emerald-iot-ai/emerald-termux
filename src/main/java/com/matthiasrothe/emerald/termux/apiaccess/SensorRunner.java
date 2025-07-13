package com.matthiasrothe.emerald.termux.apiaccess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.matthiasrothe.emerald.termux.bindings.annotations.TimedSensorReadingQueueBinding;
import com.matthiasrothe.emerald.termux.model.Status;
import com.matthiasrothe.emerald.termux.model.TimedSensorReading;
import com.matthiasrothe.emerald.termux.thread.AbortablePaecoThread;
import com.matthiasrothe.emerald.termux.thread.AbortableThread;

import net.sf.jetro.stream.visitor.LazilyParsedNumber;
import net.sf.jetro.tree.JsonArray;
import net.sf.jetro.tree.JsonNumber;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.builder.JsonTreeBuilder;

@Singleton
public class SensorRunner extends AbortablePaecoThread {
	private static final String SENSOR_NAME = "Accelerometer";
	private static final int SENSOR_DELAY = 50; //delay in milliseconds
	
	private Throwable throwable;
	private Process process;
	private BufferedReader input;
	private JsonTreeBuilder builder = new JsonTreeBuilder();
	
	private AbortableThread dummyReader;
	
	@Inject
	@TimedSensorReadingQueueBinding
	private PriorityQueue<TimedSensorReading> sensorReadingQueue;
	
	@Inject
	private Status status;
	
	@Override
	protected void prepare() {
		if (process == null) {
			try {
				Runtime.getRuntime().exec(new String[] {"termux-wake-lock"});
				process = Runtime.getRuntime().exec(
						new String[] {"termux-sensor",
								"-s", SENSOR_NAME,
								"-d", SENSOR_DELAY + ""});
				input = new BufferedReader(new InputStreamReader(process.getInputStream()));
				status.setSensorRunnerStatus("Initialized");
			} catch (IOException e) {
				throwable = e;
			}
		}
	}

	@Override
	protected void act() {
		if (throwable == null && input != null) {
			try {
				JsonObject sensorValues = (JsonObject) builder.build(nextJsonDocument());
				sensorValues.asMap().entrySet().forEach(entry -> {
					JsonObject inner = (JsonObject) entry.getValue();
					JsonArray valuesArray = (JsonArray) inner.get("values");
					List<Double> values = new ArrayList<>();
					
					valuesArray.forEach(value ->
						values.add(((LazilyParsedNumber)((JsonNumber) value).getValue()).doubleValue()));
					
					TimedSensorReading sensorReading = new TimedSensorReading(SENSOR_NAME,
							LocalDateTime.now(), values);
					
					status.addSensorReading(sensorReading);
					
					synchronized (sensorReadingQueue) {
						sensorReadingQueue.offer(sensorReading);
						sensorReadingQueue.notify();
					}
				});
			} catch (Throwable t) {
				throwable = t;
			}
		}
	}

	private String nextJsonDocument() throws IOException {
		int braceCount = 0;
		StringBuilder jsonDocument = new StringBuilder();
		
		do {
			String line = input.readLine();
			
			for (int i = 0; i < line.length(); i++) {
				char c = line.charAt(i);
				
				if (c == '{') braceCount++;
				if (c == '}') braceCount--;
			}
			
			jsonDocument.append(line);
		} while (braceCount > 0);

		return jsonDocument.toString();
	}
	
	@Override
	protected void evaluate() {
		if (throwable == null) {
			status.setSensorRunnerStatus("Running");
		} else {
			status.setSensorRunnerStatus("An error occurred: " + throwable.getClass().getName()
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
		if (shouldAbort() && process != null) {
			try {
				Runtime.getRuntime().exec(new String[] { "kill", "-SIGINT", process.pid() + "" });
				process.waitFor();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void beforePause() {
		dummyReader = new AbortableThread(() -> {
			try {
				nextJsonDocument();
			} catch (Exception e) {
			}
		});
		dummyReader.start();
		status.setSensorRunnerStatus("Paused");
	}

	@Override
	protected void afterPause() {
		try {
			dummyReader.abort();
			dummyReader.join();
		} catch (InterruptedException e) {
		}
		
		status.setSensorRunnerStatus("Running");
	}
}
