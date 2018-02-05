package se.uu.ebc.bemanning.service;

import java.util.Collection;

import se.uu.ebc.bemanning.entity.Person;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

import org.apache.log4j.Logger;


@Service
public class AKKAService {

    
    private static Logger logger = Logger.getLogger(AKKAService.class.getName());

	@Value("${akka.url}")
	String akkaUrl;


	public Map<String, String> doLookup(Person p) {

		HashMap<String,String> ldapSessionMap = new HashMap<String, String>();
		
		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");

		env.put(Context.PROVIDER_URL,
			akkaUrl);

/*
		env.put(Context.PROVIDER_URL,
			"ldap://ldap.katalog.uu.se:389");

		env.put(Context.PROVIDER_URL,
			"ldaps://ldap.katalog.uu.se:636");
		env.put(Context.SECURITY_AUTHENTICATION,
			"simple");
		env.put(Context.SECURITY_PRINCIPAL,
			"uid=mikathol,dc=user,dc=uu,dc=se");
		env.put(Context.SECURITY_CREDENTIALS, "XXXX");
*/
		
		try {

			if (p.getUsername() == null || p.getUsername().trim().length() == 0)
			{
				throw new IllegalArgumentException(
					"se.uu.ebc.courseres.service.StaffingService.getPersonAssignments(String year, String username) - 'username' can not be null or empty");
			}

			InitialLdapContext ctx = new InitialLdapContext(env, null);

			Attributes matchAttrs = new BasicAttributes(true);
			matchAttrs.put("uid",p.getUsername());

			logger.debug("doLookup finding "+ matchAttrs.get("sn")+", "+matchAttrs.get("givenname"));

			NamingEnumeration ans = ctx.search("cn=People,dc=uu,dc=se", matchAttrs);

			logger.debug("doLookup namingEnum "+ ans);


			while (ans.hasMore()) {
				SearchResult sr = (SearchResult) ans.next();
				logger.debug("doLookup sr "+ sr);
				
				Attributes attrs = sr.getAttributes();
				logger.debug("doLookup attributes "+ attrs);

				NamingEnumeration ids = attrs.getIDs();
				while (ids.hasMore()) {
					String id = (String) ids.next();
					logger.debug("doLookup id "+ id);
					
					Attribute val = attrs.get(id);
					for (int idx = 0; idx < val.size(); idx++) {
						String dir_val = (String) val.get(idx);
						ldapSessionMap.put(id, dir_val);
						logger.debug("doLookup dir_val "+ dir_val);
						
					}
				}
			}


		} catch (Exception ne) {
			ne.printStackTrace();
			logger.error("doLookup experienced pesky exception "+ ne);
		} finally {
			return ldapSessionMap;
		}

	}
	
/* 
	public String getEmail ()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("MTh getEmail");
		}
		if (ldapSessionMap == null) {
			doLookup();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("MTh getEmail, done LDAP lookup, map " + ldapSessionMap);
		}
	
		return ldapSessionMap.containsKey("mail") ? ldapSessionMap.get("mail") :"";
	}
	
	public String getPhone ()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("MTh getPhone");
		}
		if (ldapSessionMap == null) {
			doLookup();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("MTh getPhone, done LDAP lookup, map " + ldapSessionMap);
		}
	
		return ldapSessionMap.containsKey("telephoneNumber") ? ldapSessionMap.get("telephoneNumber") :"";
	}

	public String getUuUser ()
	{
		if (ldapSessionMap == null) {
			doLookup();
		}

		String uuUser = "";
		
		if (ldapSessionMap.containsKey("edupersonprincipalname")) {
			Matcher matcher = Pattern.compile("([\\w\\d]+)@(.*)").matcher(ldapSessionMap.get("edupersonprincipalname"));
		
			while (matcher.find()) {
				uuUser=matcher.group(1);
			}
		}

		return uuUser;
	}
 */
}
