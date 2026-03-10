package se.uu.ebc.bemanning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import lombok.extern.slf4j.Slf4j;


import se.uu.ebc.bemanning.ldap.repository.StaffAkkaRepository;


@Slf4j
@Service
public class TestService {

	@Autowired
	StaffAkkaRepository uuStaffRepo;


	public Object test() throws Exception {
	
		log.debug ( "ldaps got {}", uuStaffRepo.findByLastName("Thollesson")) ;
		return "Okelidokeli";
	}    
 
}