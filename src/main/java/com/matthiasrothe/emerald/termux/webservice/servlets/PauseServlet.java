package com.matthiasrothe.emerald.termux.webservice.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;

import com.google.inject.Inject;
import com.matthiasrothe.emerald.termux.bindings.annotations.RecorderClientBinding;
import com.matthiasrothe.emerald.termux.bindings.annotations.SensorRunnerBinding;
import com.matthiasrothe.emerald.termux.thread.AbortablePaecoThread;

public class PauseServlet extends HttpServlet implements Constants {
	private static final long serialVersionUID = 2487222744381758458L;
	private static final String SERVLET_RESPONSE = "{\"status\": \"application paused\"}";
	
	@Inject
	@RecorderClientBinding
	private AbortablePaecoThread recorderClient;
	
	@Inject
	@SensorRunnerBinding
	private AbortablePaecoThread sensorRunner;
	
	@Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
		sensorRunner.pause();
		recorderClient.pause();
		
		resp.setStatus(HttpStatus.OK_200);
		resp.setContentType(APPLICATION_JSON);
		resp.setContentLength(SERVLET_RESPONSE.length());
		resp.getWriter().write(SERVLET_RESPONSE);
	}
}
