package com.jchart.test.jaxrs.book.server;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:accounts@jchart.com">Paul S. Russo</a>
 */
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {
	
	public LoggingFilter() {
		System.out.println("LoggingFilter: constructor");
	}
	
	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {
		System.out.println("LoggingFilter1: requestContext");
	}

	@Override
	public void filter(ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) throws IOException {
		System.out.println("LoggingFilter2: : requestContext, responseContext");
	}
}
