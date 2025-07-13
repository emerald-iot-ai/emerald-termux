package com.matthiasrothe.emerald.termux.webservice.servlets;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Server;

import com.google.inject.Inject;
import com.matthiasrothe.emerald.termux.bindings.annotations.RecorderClientBinding;
import com.matthiasrothe.emerald.termux.bindings.annotations.SensorRunnerBinding;
import com.matthiasrothe.emerald.termux.thread.AbortablePaecoThread;

public class ShutdownServlet extends HttpServlet implements Constants {
	private static final long serialVersionUID = 3212125933394527767L;	
	private static final String SERVLET_RESPONSE = "{\"status\": \"shutdown initiated\"}";
	
	private Server server;
	
	@Inject
	@RecorderClientBinding
	private AbortablePaecoThread recorderClient;
	
	@Inject
	@SensorRunnerBinding
	private AbortablePaecoThread sensorRunner;

	public void setServer(final Server server) {
		Objects.requireNonNull(server, "server must not be null");
	}
	
	@Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
		sensorRunner.abort();
		recorderClient.abort();
		
		stopServer();
		
		resp.setStatus(HttpStatus.OK_200);
		resp.setContentType(APPLICATION_JSON);
		resp.setContentLength(SERVLET_RESPONSE.length());
		resp.getWriter().write(SERVLET_RESPONSE);
	}
	
	private void stopServer() {
		Thread t = new Thread(() -> {
			try {
				Thread.sleep(5000);
				server.stop();
			} catch (Exception e) {
				System.exit(-1);
			}
			server.destroy();
		});
		
		t.start();
	}
}
