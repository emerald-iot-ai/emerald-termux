package com.matthiasrothe.emerald.termux.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class TimedSensorReading implements Comparable<TimedSensorReading> {
	private final String sensorName;
	private final LocalDateTime timestamp;
	private final List<Double> values;
	
	public TimedSensorReading(final String sensorName, final LocalDateTime timestamp, final List<Double> values) {
		Objects.requireNonNull(sensorName, "sensorName must not be null");
		Objects.requireNonNull(timestamp, "timestamp must not be null");
		Objects.requireNonNull(values, "values must not be null");
		
		if (values.size() == 0) {
			throw new IllegalArgumentException("values must not be empty");
		}
		
		this.sensorName = sensorName;
		this.timestamp = timestamp;
		this.values = values;
	}

	public String getSensorName() {
		return sensorName;
	}
	
	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public List<Double> getValues() {
		return values;
	}

	@Override
	public String toString() {
		return "TimedSensorReading [sensorName=" + sensorName + ", timestamp=" + timestamp + ", values=" + values + "]";
	}

	@Override
	public int compareTo(TimedSensorReading o) {
		return timestamp.compareTo(o.timestamp);
	}
}
