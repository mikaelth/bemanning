package se.uu.ebc.bemanning;

import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import se.uu.ebc.bemanning.security.BemanningUserService;
import se.uu.ebc.bemanning.security.SecurityService;
import se.uu.ebc.bemanning.security.RESTAuthenticationEntryPoint;
/* 
import lombok.extern.slf4j.Slf4j;

@Slf4j
 */
@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@EnableAutoConfiguration
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class BemanningSecurityConfig extends WebSecurityConfigurerAdapter {

	static Logger log = Logger.getLogger(BemanningSecurityConfig.class.getName());

	@Value("${bemanning.base.url}")
	String baseUrl;

	@Value("${bemanning.environment.dev}")
	boolean devEnv = false;

	@Bean
    public AuthenticationUserDetailsService authenticationUserDetailsService() {
		log.debug("authenticationUserDetailsService()");
        return new SecurityService();
    }

    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService(baseUrl + "/login/cas");
        serviceProperties.setSendRenew(false);
		log.debug("serviceProperties() " + serviceProperties);
        return serviceProperties;
    }

    @Bean
    public CasAuthenticationProvider casAuthenticationProvider() {
		log.debug("authenticationUserDetailsService()");
        CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
        casAuthenticationProvider.setAuthenticationUserDetailsService(authenticationUserDetailsService());
        casAuthenticationProvider.setServiceProperties(serviceProperties());
        casAuthenticationProvider.setTicketValidator(cas20ServiceTicketValidator());
        casAuthenticationProvider.setKey("ThisIsSomeKindOfKey");
		log.debug("casAuthenticationProvider() " + casAuthenticationProvider);
        return casAuthenticationProvider;
    }

    @Bean
    public Cas20ServiceTicketValidator cas20ServiceTicketValidator() {
//        return new Cas20ServiceTicketValidator("https://cas.weblogin.uu.se/cas");
        return new Cas20ServiceTicketValidator("https://weblogin.uu.se/idp/profile/cas");
    }

    @Bean
    public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
        CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
        casAuthenticationFilter.setAuthenticationManager(authenticationManager());
		log.debug("casAuthenticationFilter() " + casAuthenticationFilter);
        return casAuthenticationFilter;
    }


    @Bean
    public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
        CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
//        casAuthenticationEntryPoint.setLoginUrl("https://cas.weblogin.uu.se/cas/login");
        casAuthenticationEntryPoint.setLoginUrl("https://weblogin.uu.se/idp/profile/cas/login");
        casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
        return casAuthenticationEntryPoint;
    }
 
 
 
    @Bean
    public RESTAuthenticationEntryPoint restcasAuthenticationEntryPoint() {
        RESTAuthenticationEntryPoint restcasAuthenticationEntryPoint = new RESTAuthenticationEntryPoint();
        restcasAuthenticationEntryPoint.setCasAuthenticationEntryPoint(casAuthenticationEntryPoint());
        return restcasAuthenticationEntryPoint;
    }
 

 
   @Override
    protected void configure(HttpSecurity http) throws Exception {
		log.debug("configure()");
 
        http
            .addFilter(casAuthenticationFilter()).csrf().disable();

 		http.csrf().disable();

 
		if (devEnv) {
			http
				.authorizeRequests().antMatchers("/**").permitAll();
		} else {

		
			http.authorizeRequests()
				.antMatchers("/index.*").authenticated()
				.antMatchers("/loginredirect.html").authenticated()
				.antMatchers("/InREST.html").authenticated()
				.antMatchers("/login/**").permitAll()
				.antMatchers("/Bemanning/index.html").authenticated()
				.antMatchers("/Bemanning/**").permitAll()
				.antMatchers("/rest/**").authenticated()
				.antMatchers("/**").authenticated();
		}
 				

 			
        http
            .exceptionHandling()
                .authenticationEntryPoint(restcasAuthenticationEntryPoint());
    }
 
//    @Override
    @Autowired
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		log.debug("configureGloabal() " + auth);
        auth
            .authenticationProvider(casAuthenticationProvider());
    }



}