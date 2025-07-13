package com.matthiasrothe.emerald.termux.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Status {
	private String sensorRunnerStatus;
	private List<TimedSensorReading> lastSensorReadings = new LinkedList<>();
	
	private String recorderClientStatus;
	private long dataPointsSuccessfullySent = 0;
	
	public synchronized String getSensorRunnerStatus() {
		return sensorRunnerStatus;
	}
	
	public synchronized void setSensorRunnerStatus(final String sensorRunnerStatus) {
		Objects.requireNonNull(sensorRunnerStatus, "sensorRunnerStatus must not be null");
		this.sensorRunnerStatus = sensorRunnerStatus;
	}
	
	public synchronized List<TimedSensorReading> getLastSensorReadings() {
		return Collections.unmodifiableList(lastSensorReadings);
	}
	
	public synchronized void addSensorReading(final TimedSensorReading sensorReading) {
		Objects.requireNonNull(sensorReading, "sensorReading must not be null");
				
		if (lastSensorReadings.size() == 5) {
			lastSensorReadings.remove(0);
		}
		
		lastSensorReadings.add(sensorReading);
	}

	public synchronized String getRecorderClientStatus() {
		return recorderClientStatus;
	}
	
	public synchronized void setRecorderClientStatus(final String recorderClientStatus) {
		Objects.requireNonNull(recorderClientStatus, "recorderClientStatus must not be null");
		this.recorderClientStatus = recorderClientStatus;
	}
	
	public synchronized long getDataPointsSuccessfullySent() {
		return dataPointsSuccessfullySent;
	}
	
	public synchronized void addDataPointsSuccessfullySent(final int count) {
		dataPointsSuccessfullySent += count;
	}
}
