package se.uu.ebc.bemanning.service;


import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.stream.Collectors;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.ContainerCriteria;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;
import static org.springframework.ldap.query.LdapQueryBuilder.query;

import se.uu.ebc.bemanning.ldap.repository.StaffAkkaRepository;
import se.uu.ebc.bemanning.ldap.model.StaffAkka;

import lombok.extern.slf4j.Slf4j;



//import se.uu.ebc.ldap.Staff;

@Service
@Slf4j
public class AKKAService {

	@Autowired
	StaffAkkaRepository uuStaffRepo;

	/* For testing */
//     private Map<String, List<String>> enumMap = new HashMap<String, List<String>>();
// 	{
// 		enumMap.put( "196304115196", new ArrayList<String>() );
// 		enumMap.put( "196810140480", new ArrayList<String>() );
// 
// 		enumMap.get("196304115196").add("N0-346_4");
// 		enumMap.get("196304115196").add("N0-346_3");
// 		enumMap.get("196304115196").add("N0-346_2");
// 		enumMap.get("196810140480").add("N6-1497_4");
// 		enumMap.get("196810140480").add("N6-1497_3");
// 	}


 	public List<String> findEmployeenumberBypNIN (String pNIN) throws Exception {
	
 
		return uuStaffRepo.findByPNIN(pNIN).stream()
			.map(StaffAkka::getEmployeeNumber)
			.toList();


		/* For testing */
// 		log.debug("Looking for {}",pNIN);
// 		if (enumMap.containsKey(pNIN)) {
// 			return enumMap.get(pNIN);
// 		} else {
// 			return new ArrayList<String>();
// 		}
	}    
}
