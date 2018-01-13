package se.uu.ebc.bemanning.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.apache.log4j.Logger;

//@Component
public class RESTAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private static Logger logger = Logger.getLogger(RESTAuthenticationEntryPoint.class.getName());
	
	private CasAuthenticationEntryPoint casAuthenticationEntryPoint;

	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {

		if (request.getRequestURI().matches("^(/bemanning)?/rest/(.*)")) { 
			logger.debug("commence with REST, uri= "+ request.getRequestURI());
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		} else {
			logger.debug("commence with CAS redirect, uri= "+ request.getRequestURI());
			casAuthenticationEntryPoint.commence(request, response, authException);
		}
	}



	public CasAuthenticationEntryPoint getCasAuthenticationEntryPoint()
	{
		return this.casAuthenticationEntryPoint;
	}

	public void setCasAuthenticationEntryPoint(CasAuthenticationEntryPoint casAuthenticationEntryPoint)
	{
		this.casAuthenticationEntryPoint = casAuthenticationEntryPoint;
	}


}