package com.matthiasrothe.emerald.termux.webservice.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;

import com.google.inject.Inject;
import com.matthiasrothe.emerald.termux.model.Status;
import com.matthiasrothe.emerald.termux.model.TimedSensorReading;

import net.sf.jetro.tree.JsonArray;
import net.sf.jetro.tree.JsonNumber;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.JsonProperty;

public class StatusServlet extends HttpServlet implements Constants {
	private static final long serialVersionUID = -2884967227479141073L;
	
	@Inject
	private Status status;
	
	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) 
			throws ServletException, IOException {
		JsonArray lastSensorReadings = new JsonArray();
		List<TimedSensorReading> sensorReadings = status.getLastSensorReadings();
		
		for (TimedSensorReading sensorReading : sensorReadings) {
			JsonArray values = new JsonArray();
			sensorReading.getValues().forEach(value -> values.add(new JsonNumber(value)));
			
			JsonObject reading = new JsonObject();
			reading.add(new JsonProperty("sensorName", sensorReading.getSensorName()));
			reading.add(new JsonProperty("timestamp", sensorReading.getTimestamp().toString()));
			reading.add(new JsonProperty("values", values));
			
			lastSensorReadings.add(reading);
		}
		
		JsonObject sensorRunnerStatus = new JsonObject();
		sensorRunnerStatus.add(new JsonProperty("status", status.getSensorRunnerStatus()));
		sensorRunnerStatus.add(new JsonProperty("lastSensorReadings", lastSensorReadings));

		JsonObject recorderClientStatus = new JsonObject();
		recorderClientStatus.add(new JsonProperty("status", status.getRecorderClientStatus()));
		recorderClientStatus.add(new JsonProperty("dataPointsSuccessfullySent",
				status.getDataPointsSuccessfullySent()));
		
		JsonObject value = new JsonObject();
		value.add(new JsonProperty("sensorRunnerStatus", sensorRunnerStatus));
		value.add(new JsonProperty("recorderClientStatus", recorderClientStatus));
		
		String json = value.toJson();
		
		resp.setStatus(HttpStatus.OK_200);
		resp.setContentType(APPLICATION_JSON);
		resp.setContentLength(json.length());
		resp.getWriter().write(json);
	}
}
