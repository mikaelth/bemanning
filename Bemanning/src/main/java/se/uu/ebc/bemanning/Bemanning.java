package se.uu.ebc.bemanning;

//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.SpringApplication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.scheduling.annotation.EnableScheduling;

import org.springframework.stereotype.Controller;

import org.springframework.transaction.annotation.EnableTransactionManagement;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import flexjson.JSONSerializer;
import flexjson.JSONDeserializer;
import flexjson.transformer.DateTransformer;

import java.security.Principal;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import se.uu.ebc.bemanning.security.RESTAuthenticationEntryPoint;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@PropertySource("classpath:application.properties")
@EnableTransactionManagement
@EnableScheduling
@EnableAutoConfiguration
@Configuration
@EnableWebMvc //Disable this and file upload will not work; enable and static will not work.
@RestController("/")
@SpringBootApplication
@CrossOrigin(origins = "http://localhost:1841")
@Import({StaticResourceConfiguration.class, BemanningSecurityConfig.class})
@ComponentScan(basePackages = {"se.uu.ebc.bemanning.service","se.uu.ebc.bemanning.web","se.uu.ebc.bemanning.security"})
//public class Bemanning extends WebMvcConfigurerAdapter {		/* Testing with embedded server */
public class Bemanning extends SpringBootServletInitializer { /* Deploying to Tomcat container */

    private static Logger logger = Logger.getLogger(Bemanning.class.getName());

	private static String trustPath = System.getenv("JAVA_HOME") + "/jre/lib/security/cacerts";

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Bemanning.class);
    }


	@Bean
	public InternalResourceViewResolver defaultViewResolver() {
		return new InternalResourceViewResolver();
	}	

	public static void main(String[] args) throws Exception {
		
//		System.setProperty("javax.net.ssl.trustStore", trustPath);
		SpringApplication.run(Bemanning.class, args);
	}

/* 
	@Bean
    public WebSecurityConfigurerAdapter webSecurityConfigurerAdapter() {
        return new BemanningSecurityConfig();
    }
 */
 
	@Bean
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver resolver=new CommonsMultipartResolver();
		resolver.setDefaultEncoding("utf-8");
		return resolver;
	}


	@RequestMapping("/")
	public RedirectView directToIndex() 
	{
    	RedirectView redirectView = new RedirectView();
    	redirectView.setUrl("index.html");
    	return redirectView;
	}


}