package se.uu.ebc.bemanning.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * Disabled Spring Security configuration for Bemanning application with CAS integration.
 * 
 * This is a simplified version that doesn't require the full Apereo CAS client library.
 * Use this configuration if you have issues with CAS client dependencies.
 * 
 * To use this configuration instead of the full one, set the profile to 'basic':
 * spring.profiles.active=basic
 */
@Configuration
@EnableWebSecurity
@Slf4j
@Profile("dev")
public class BemanningDevSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http
			// Vaadin's framework requests (UIDL, heartbeat, dev-tools/push) are POSTed
			// to the servlet root (e.g. /?v-r=uidl). Spring Security's CSRF filter was
			// rejecting those with HTTP 403, which the browser surfaces as
			// "Connection lost, trying to reconnect". Vaadin has its own CSRF protection
			// for these requests (the Vaadin-Security-Key for UIDL and the Vaadin-Push-ID
			// for the websocket), so Spring Security CSRF must not also guard them.
			// In this dev profile everything is permitAll for local development, so we
			// disable Spring Security CSRF entirely here. (In the prod/CAS config, prefer
			// Vaadin's VaadinSecurityConfigurer, which ignores CSRF for framework requests
			// while keeping it enabled elsewhere.)
			.csrf(csrf -> csrf.disable())
        	.build();
    }
}