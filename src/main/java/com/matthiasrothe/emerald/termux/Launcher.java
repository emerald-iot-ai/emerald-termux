package com.matthiasrothe.emerald.termux;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.matthiasrothe.emerald.termux.bindings.annotations.RecorderClientBinding;
import com.matthiasrothe.emerald.termux.bindings.annotations.SensorRunnerBinding;
import com.matthiasrothe.emerald.termux.bindings.annotations.WebserviceRunnerBinding;
import com.matthiasrothe.emerald.termux.bindings.injections.Injections;
import com.matthiasrothe.emerald.termux.thread.Startable;

public class Launcher {
	
	@Inject
	@RecorderClientBinding
	private Startable recorderClient;
	
	@Inject
	@SensorRunnerBinding
	private Startable sensorRunner;
	
	@Inject
	@WebserviceRunnerBinding
	private Startable webserviceRunner;
	
	private void launchApplication() {
		recorderClient.start();
		sensorRunner.start();
		webserviceRunner.start();
	}
	
	public static void main(String[] args) {
		Launcher launcher = new Launcher();
		Guice.createInjector(new Injections()).injectMembers(launcher);
		launcher.launchApplication();
	}
}
