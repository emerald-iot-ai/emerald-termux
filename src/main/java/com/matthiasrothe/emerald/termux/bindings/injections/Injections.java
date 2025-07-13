package com.matthiasrothe.emerald.termux.bindings.injections;

import java.util.PriorityQueue;

import javax.servlet.http.HttpServlet;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.matthiasrothe.emerald.termux.apiaccess.SensorRunner;
import com.matthiasrothe.emerald.termux.bindings.annotations.PauseServletBinding;
import com.matthiasrothe.emerald.termux.bindings.annotations.RecorderClientBinding;
import com.matthiasrothe.emerald.termux.bindings.annotations.ResumeServletBinding;
import com.matthiasrothe.emerald.termux.bindings.annotations.SensorRunnerBinding;
import com.matthiasrothe.emerald.termux.bindings.annotations.ShutdownServletBinding;
import com.matthiasrothe.emerald.termux.bindings.annotations.StatusServletBinding;
import com.matthiasrothe.emerald.termux.bindings.annotations.TimedSensorReadingQueueBinding;
import com.matthiasrothe.emerald.termux.bindings.annotations.WebserviceRunnerBinding;
import com.matthiasrothe.emerald.termux.dl4j.recorder.client.RecorderClient;
import com.matthiasrothe.emerald.termux.model.Status;
import com.matthiasrothe.emerald.termux.model.TimedSensorReading;
import com.matthiasrothe.emerald.termux.thread.AbortablePaecoThread;
import com.matthiasrothe.emerald.termux.thread.Startable;
import com.matthiasrothe.emerald.termux.webservice.WebserviceRunner;
import com.matthiasrothe.emerald.termux.webservice.servlets.PauseServlet;
import com.matthiasrothe.emerald.termux.webservice.servlets.ResumeServlet;
import com.matthiasrothe.emerald.termux.webservice.servlets.ShutdownServlet;
import com.matthiasrothe.emerald.termux.webservice.servlets.StatusServlet;

public class Injections implements Module {

	@Override
	public void configure(Binder binder) {
		
		binder.bind(Status.class).toInstance(new Status());
		
		//Servlets
		binder.bind(HttpServlet.class).annotatedWith(StatusServletBinding.class).to(StatusServlet.class);
		binder.bind(HttpServlet.class).annotatedWith(PauseServletBinding.class).to(PauseServlet.class);
		binder.bind(HttpServlet.class).annotatedWith(ResumeServletBinding.class).to(ResumeServlet.class);
		binder.bind(HttpServlet.class).annotatedWith(ShutdownServletBinding.class).to(ShutdownServlet.class);
		
		//Threads
		binder.bind(Startable.class).annotatedWith(SensorRunnerBinding.class).to(SensorRunner.class);
		binder.bind(AbortablePaecoThread.class).annotatedWith(SensorRunnerBinding.class).to(SensorRunner.class);
		
		binder.bind(Startable.class).annotatedWith(RecorderClientBinding.class).to(RecorderClient.class);
		binder.bind(AbortablePaecoThread.class).annotatedWith(RecorderClientBinding.class).to(RecorderClient.class);
		
		binder.bind(Startable.class).annotatedWith(WebserviceRunnerBinding.class).to(WebserviceRunner.class);
		
		//Queues
		binder.bind(new TypeLiteral<PriorityQueue<TimedSensorReading>>() {})
			.annotatedWith(TimedSensorReadingQueueBinding.class).toInstance(new PriorityQueue<TimedSensorReading>());
	}

}
