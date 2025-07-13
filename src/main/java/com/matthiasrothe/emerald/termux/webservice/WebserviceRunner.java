package com.matthiasrothe.emerald.termux.webservice;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.matthiasrothe.emerald.termux.bindings.annotations.PauseServletBinding;
import com.matthiasrothe.emerald.termux.bindings.annotations.ResumeServletBinding;
import com.matthiasrothe.emerald.termux.bindings.annotations.ShutdownServletBinding;
import com.matthiasrothe.emerald.termux.bindings.annotations.StatusServletBinding;
import com.matthiasrothe.emerald.termux.thread.Startable;
import com.matthiasrothe.emerald.termux.webservice.servlets.ShutdownServlet;

@Singleton
public class WebserviceRunner extends Thread implements Startable {
	
	@Inject
	@StatusServletBinding
	private HttpServlet statusServlet;
	
	@Inject
	@PauseServletBinding
	private HttpServlet pauseServlet;
	
	@Inject
	@ResumeServletBinding
	private HttpServlet resumeServlet;
	
	@Inject
	@ShutdownServletBinding
	private HttpServlet shutdownServlet;
	
	@Override
	public void run() {
        try {
	        Server server = new Server(8080);
	        
	        ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
	        
	        ctx.setContextPath("/");
	        server.setHandler(ctx);

	        ServletHolder serHol = new ServletHolder(statusServlet);
	        ctx.addServlet(serHol, "/emerald-termux/api/status");

	        serHol = new ServletHolder(pauseServlet);
	        ctx.addServlet(serHol, "/emerald-termux/api/pause");

	        serHol = new ServletHolder(resumeServlet);
	        ctx.addServlet(serHol, "/emerald-termux/api/resume");
	        
	        ((ShutdownServlet) shutdownServlet).setServer(server);
	        serHol = new ServletHolder(shutdownServlet);
	        ctx.addServlet(serHol, "/emerald-termux/api/shutdown");
	
            server.start();
            server.join();
        } catch (Exception ex) {
            Logger.getLogger(WebserviceRunner.class.getName()).log(Level.SEVERE, "Couldn't start webserver.", ex);
        }
	}
}
