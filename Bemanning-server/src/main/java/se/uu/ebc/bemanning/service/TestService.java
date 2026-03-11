package se.uu.ebc.bemanning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import lombok.extern.slf4j.Slf4j;


import se.uu.ebc.bemanning.ldap.repository.StaffAkkaRepository;
import se.uu.ebc.bemanning.repo.AkkaStaffRepo;


@Slf4j
@Service
public class TestService {

	@Autowired
	StaffAkkaRepository uuStaffRepo;

	@Autowired
	AkkaStaffRepo akkaStaffRepo;


	public Object test() throws Exception {
	
		log.debug ( "ldaps got {}", uuStaffRepo.findByLastName("Thollesson")) ;
		return "Okelidokeli";
	}    

 	public Object testempnum() throws Exception {
	
		log.debug ( "Got {}", akkaStaffRepo.findUserByEmployeeNumberAndYear("N0-346_2", "2026").get().getPerson().getName()) ;
		return "Okelidokeli";
	}    

}