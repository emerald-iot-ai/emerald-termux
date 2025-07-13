package com.matthiasrothe.emerald.termux.multicast;

import java.util.ArrayList;
import java.util.List;

public abstract class Multicaster<T> {
	private List<ValueListener<T>> listeners = new ArrayList<>();
	
	public void addListener(final ValueListener<T> listener) {
		listeners.add(listener);
	}
	
	public void removeListener(final ValueListener<T> listener) {
		listeners.remove(listener);
	}
	
	public void multicastValue(final T value) {
		listeners.forEach(listener -> listener.onValue(value));
	}
}
