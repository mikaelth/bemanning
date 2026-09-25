package se.uu.ebc.bemanning;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;

import org.springframework.web.client.RestClient;

import org.springframework.core.env.Environment;

//import org.springframework.ldap.core.LdapTemplate;
//import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.data.ldap.repository.config.EnableLdapRepositories;
//import org.springframework.web.service.registry.ImportHttpServices;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
//import org.springframework.context.annotation.Configuration;

//import se.uu.ebc.bemanning.security.SecurityService;
import se.uu.ebc.bemanning.service.ColumnHeadersRecord;
//import se.uu.ebc.bemanning.service.PeopleService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableLdapRepositories(basePackages = "se.uu.ebc.ldap")
//@Configuration
//@ConfigurationPropertiesScan({"se.uu.ebc.bemanning"})
@EnableConfigurationProperties(ColumnHeadersRecord.class)
//@PropertySource("classpath:application.properties")
//@EnableTransactionManagement
//@EnableJpaAuditing(auditorAwareRef="auditorProvider")
//@EnableScheduling
//@EnableAutoConfiguration
//@RestController("/")
//@CrossOrigin(origins = "http://localhost:1841")
//@Import({StaticResourceConfiguration.class, LuntanSecurityConfig.class, LuntanMethodSecurityConfig.class})
//@ComponentScan(basePackages = {"se.uu.ebc.bemanning.service","se.uu.ebc.bemanning.controller","se.uu.ebc.bemanning.security"})
@SpringBootApplication
//@ImportHttpServices(basePackages = "se.uu.ebc.bemanning.controller")
public class Bemanning /* extends SpringBootServletInitializer { Deploying to Tomcat container */ {
	
	@Value("${luntan.rest.base.url}")
	String luntanBaseUrl;

    //@Autowired
    private final Environment env;
    // Spring automatically injects this dependency!
	public Bemanning(Environment env) {
		this.env = env;
	}

	@Bean
	public RestClient luntanCIRestClient() {
		RestClient luntanRestClient = RestClient.create(luntanBaseUrl+"cis/");

		return luntanRestClient;
	}

	/* LDAP is autoconfigured; context and template */
	
	
	public static void main(String[] args) {
		SpringApplication.run(Bemanning.class, args);
	}

	// NOTE: The previous @RequestMapping("/") that redirected the app root to the
	// legacy ExtJS SPA (index.html) was removed. With React routing enabled, the
	// Vaadin servlet must own the root context (/*), and Vaadin issues its
	// bootstrap/UIDL requests against "/" (e.g. /?v-r=init). A Spring MVC mapping
	// on "/" would intercept those requests and cause "Connection lost, trying to
	// reconnect". The legacy ExtJS SPA is still reachable directly at
	// /index.html (served as a static resource).

}
