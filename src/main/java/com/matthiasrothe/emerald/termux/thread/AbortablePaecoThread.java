package com.matthiasrothe.emerald.termux.thread;

public abstract class AbortablePaecoThread extends Thread implements Startable {
	private boolean abort;
	private Object abortMutex = new Object();
	
	private boolean pause;
	private Object pauseMutex = new Object();
	
	protected abstract void prepare();
	
	protected abstract void act();
	
	protected abstract void evaluate();
	
	protected abstract void consolidate();
	
	protected abstract void optimize();
	
	protected abstract void beforePause();
	
	protected abstract void afterPause();
	
	public void abort() {
		synchronized (abortMutex) {
			abort = true;
			unpause();
		}
	}

	public void pause() {
		synchronized (pauseMutex) {
			pause = true;
		}
	}
	
	public void unpause() {
		synchronized (pauseMutex) {
			pause = false;
			pauseMutex.notify();
		}
	}
	
	@Override
	public void run() {
		while (!shouldAbort()) {
			prepare();
			pauseIfRequested();
			
			act();
			pauseIfRequested();
			
			evaluate();
			pauseIfRequested();
			
			consolidate();
			pauseIfRequested();
			
			optimize();
			pauseIfRequested();
		}
	}
	
	protected boolean shouldAbort() {
		synchronized (abortMutex) {
			return abort;
		}
	}
	
	private void pauseIfRequested() {
		synchronized (pauseMutex) {
			if (pause) {
				try {
					beforePause();
					pauseMutex.wait();
					afterPause();
				} catch (InterruptedException e) {
				}
			}
		}
	}
}
