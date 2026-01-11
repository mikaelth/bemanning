package se.uu.ebc.bemanning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import se.uu.ebc.ldap.entity.UUStaff;
import se.uu.ebc.ldap.repo.UUStaffRepo;


@Service
public class TestService {

	@Autowired
	UUStaffRepo uuStaffRepo;


	public String test() throws Exception {
	
		UUStaff theStaff = uuStaffRepo.findEmployeeByMail("thollesson@ebc.uu.se");
		
		return theStaff.getDepartment();
	}    

 
}