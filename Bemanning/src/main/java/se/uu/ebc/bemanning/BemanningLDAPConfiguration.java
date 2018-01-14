package se.uu.ebc.bemanning;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import org.springframework.data.ldap.repository.config.EnableLdapRepositories;
import org.springframework.data.ldap.repository.support.LdapRepositoryFactoryBean;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.ContextSource;
import org.apache.log4j.Logger;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Configuration
@EnableLdapRepositories(basePackages = "se.uu.ebc.bemanning.ldap.AKKAUserRepo")
public class BemanningLDAPConfiguration {

    private static Logger logger = Logger.getLogger(BemanningLDAPConfiguration.class.getName());

    @Bean
    ContextSource contextSource() {

        LdapContextSource ldapContextSource = new LdapContextSource();
        ldapContextSource.setUrl("ldap://ldap.katalog.uu.se:389");

        return ldapContextSource;
    }

    @Bean
    LdapTemplate ldapTemplate(ContextSource contextSource) {
        return new LdapTemplate(contextSource);
    }
}