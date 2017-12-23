package se.uu.ebc.bemanning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import javax.activation.MimetypesFileTypeMap;
import se.uu.ebc.bemanning.entity.Person;
import se.uu.ebc.bemanning.entity.Staff;
import se.uu.ebc.bemanning.entity.Assignment;
import se.uu.ebc.bemanning.security.UserRepo;


@Service
public class TestService {

	@Autowired
	UserRepo userRepo;


	public void test() throws Exception {
	
		for (Person p : userRepo.findAll() ) {
	
			System.out.println("Person: " + p.getName());
			
			for (Staff s : p.getStaff()) {
				System.out.println("Staff: " + s.getYear());
				
				for (Assignment a : s.getAssignments()) {
					System.out.println( p.getName()+": "+s.getYear() +", " + a.getCourseInstance().getCourse().getSeName()+", " + a.getTotalHours(s.getPosition()));
				}

			}
				
		}
	}    

 
}