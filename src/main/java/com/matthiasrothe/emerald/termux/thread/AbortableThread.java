package com.matthiasrothe.emerald.termux.thread;

public class AbortableThread extends Thread {
	private final Runnable runnable;
	
	private boolean abort;
	private Object abortMutex = new Object();
	
	public AbortableThread(final Runnable runnable) {
		if (runnable != null) {
			this.runnable = runnable;
		} else {
			this.runnable = () -> {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}	
			};
		}
	}
	
	public void abort() {
		synchronized (abortMutex) {
			abort = true;
		}
	}
	
	@Override
	public void run() {
		while (!shouldAbort()) {
			runnable.run();
		}
	}
	
	private boolean shouldAbort() {
		synchronized (abortMutex) {
			return abort;
		}
	}
}
