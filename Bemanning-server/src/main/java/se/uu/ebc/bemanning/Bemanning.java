package se.uu.ebc.bemanning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;

import org.springframework.web.client.RestClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import org.springframework.core.env.Environment;

import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.data.ldap.repository.config.EnableLdapRepositories;
import org.springframework.web.service.registry.ImportHttpServices;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

import se.uu.ebc.bemanning.service.ColumnHeadersRecord;

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

    @Autowired
    private Environment env;

	@Bean
	public RestClient luntanCIRestClient() {
		RestClient luntanRestClient = RestClient.create(luntanBaseUrl+"cis/");

		return luntanRestClient;
	}

	/* LDAP is autoconfigured; context and template */
	
	
	public static void main(String[] args) {
		SpringApplication.run(Bemanning.class, args);
	}


	@RequestMapping("/")
	public RedirectView directToIndex()
	{
    	RedirectView redirectView = new RedirectView();
    	redirectView.setUrl("index.html");
    	return redirectView;
	}

}
