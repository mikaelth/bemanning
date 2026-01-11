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
			.csrf(csrf -> csrf.ignoringRequestMatchers("/rest/**")) // Disable CSRF for REST endpoints
        	.build();
    }
}