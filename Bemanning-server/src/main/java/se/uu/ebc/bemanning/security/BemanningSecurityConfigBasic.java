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
 * Basic Spring Security configuration for Bemanning application with CAS integration.
 * 
 * This is a simplified version that doesn't require the full Apereo CAS client library.
 * Use this configuration if you have issues with CAS client dependencies.
 * 
 * To use this configuration instead of the full one, set the profile to 'basic':
 * spring.profiles.active=basic
 */
@Configuration
@EnableWebSecurity
@Profile("basic & prod")
@Slf4j
public class BemanningSecurityConfigBasic {

    @Value("${cas.server.url:https://cas.uu.se/cas}")
    private String casServerUrl;

    @Value("${cas.server.login.url:https://cas.uu.se/cas/login}")
    private String casServerLoginUrl;

    @Value("${cas.server.logout.url:https://cas.uu.se/cas/logout}")
    private String casServerLogoutUrl;

    @Value("${app.server.url:http://localhost:8080}")
    private String appServerUrl;

    @Value("${app.service.security:bemanning}")
    private String appServiceSecurity;

    @Autowired
    private BemanningUserService bemanningUserService;

    /**
     * Basic security filter chain configuration without advanced CAS features
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/", "/login", "/logout", "/error").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                
                // REST API endpoints - require authentication
                .requestMatchers("/rest/**").authenticated()
                
                // Admin endpoints - require ADMIN role
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            
            // CAS configuration
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(casAuthenticationEntryPoint())
            )
            
            // Add CAS filter
            .addFilter(casAuthenticationFilter())
            
            // Logout configuration
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl(casServerLogoutUrl + "?service=" + appServerUrl)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            
            // CSRF configuration
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/rest/**") // Disable CSRF for REST endpoints
            )
            
            // Session management
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )
            
            // Headers configuration for security
            .headers(headers -> headers
//                .frameOptions().deny()
//                .contentTypeOptions().and()
                .httpStrictTransportSecurity(hsts -> hsts
                    .maxAgeInSeconds(31536000)
//                    .includeSubdomains(true)
                )
            );

        return http.build();
    }

    /**
     * Service properties for CAS
     */
    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService(appServerUrl + "/" + appServiceSecurity + "/login/cas");
        serviceProperties.setSendRenew(false);
        return serviceProperties;
    }

    /**
     * CAS authentication entry point
     */
    @Bean
    public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
        CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
        casAuthenticationEntryPoint.setLoginUrl(casServerLoginUrl);
        casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
        return casAuthenticationEntryPoint;
    }

    /**
     * Basic CAS authentication provider (without ticket validator)
     */
    @Bean
    public CasAuthenticationProvider casAuthenticationProvider() {
        CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
        casAuthenticationProvider.setAuthenticationUserDetailsService(bemanningUserService);
        casAuthenticationProvider.setServiceProperties(serviceProperties());
        // Note: No ticket validator in basic configuration
        casAuthenticationProvider.setKey("casAuthenticationProviderKey");
        return casAuthenticationProvider;
    }

    /**
     * Authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Arrays.asList(casAuthenticationProvider()));
    }

    /**
     * CAS authentication filter
     */
    @Bean
    public CasAuthenticationFilter casAuthenticationFilter() {
        CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
        casAuthenticationFilter.setAuthenticationManager(authenticationManager());
        casAuthenticationFilter.setFilterProcessesUrl("/login/cas");
        return casAuthenticationFilter;
    }
}