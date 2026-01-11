package se.uu.ebc.bemanning.security;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

//@Component
@Data
@Slf4j
public class RESTAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private CasAuthenticationEntryPoint casAuthenticationEntryPoint;

	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {

		if (request.getRequestURI().matches("^(/bemanning)?/rest/(.*)")) { 
			log.debug("commence with REST, uri= "+ request.getRequestURI());
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		} else {
			log.debug("commence with CAS redirect, uri= "+ request.getRequestURI());
			casAuthenticationEntryPoint.commence(request, response, authException);
		}
	}






}