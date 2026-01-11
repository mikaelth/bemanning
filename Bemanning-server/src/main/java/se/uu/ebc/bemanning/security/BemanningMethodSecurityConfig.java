package se.uu.ebc.bemanning.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;


@Configuration
@Profile("prod & !dev")
@EnableMethodSecurity(
	prePostEnabled = true,
	securedEnabled = true,
	jsr250Enabled = true
)
public class BemanningMethodSecurityConfig {
//public class BemanningMethodSecurityConfig extends GlobalMethodSecurityConfiguration {

}

