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

public class ResumeServlet extends HttpServlet implements Constants {
	private static final long serialVersionUID = -4233380918514569104L;
	private static final String SERVLET_RESPONSE = "{\"status\": \"application resumed\"}";
	
	@Inject
	@RecorderClientBinding
	private AbortablePaecoThread recorderClient;
	
	@Inject
	@SensorRunnerBinding
	private AbortablePaecoThread sensorRunner;
	
	@Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
		recorderClient.unpause();
		sensorRunner.unpause();
		
		resp.setStatus(HttpStatus.OK_200);
		resp.setContentType(APPLICATION_JSON);
		resp.setContentLength(SERVLET_RESPONSE.length());
		resp.getWriter().write(SERVLET_RESPONSE);
	}
}
