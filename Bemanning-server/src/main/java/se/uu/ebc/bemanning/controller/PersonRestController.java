package se.uu.ebc.bemanning.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import lombok.extern.slf4j.Slf4j;


import se.uu.ebc.bemanning.security.SecurityService;
import se.uu.ebc.bemanning.dto.PersonDTO;
import se.uu.ebc.bemanning.dto.UserDTO;
import se.uu.ebc.bemanning.enums.UserRoles;
import se.uu.ebc.bemanning.service.PeopleService;

import java.util.List;
import java.util.HashSet;
import java.util.HashMap;
import java.time.Year;

import jakarta.validation.Valid;


@RestController
@RequestMapping(value = "/rest")
@CrossOrigin(origins = "http://localhost:1841")
@Slf4j
public class PersonRestController {



    // Spring automatically injects this dependency!
	private final SecurityService securityService;
	private final PeopleService peopleService;

	public PersonRestController(SecurityService securityService, PeopleService peopleService) {
		this.securityService = securityService;
		this.peopleService = peopleService;
	}

	private record DeleteStatus (Boolean sucess, Long id) {}
	private record CreatePersonStatus (Boolean sucess, PersonDTO people) {}
	private record People (List<PersonDTO> people) {};


	/* Persons */	

    @GetMapping(value="/people")
    public ResponseEntity<People> getAllEntities() {
		return ResponseEntity.ok(new People (peopleService.getAllPersons() ));
    }

    @GetMapping(value="/people/{id}")
    public PersonDTO getEntity(@PathVariable Long id) {
		return peopleService.getById(id);
    }

	@PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    @PostMapping(value="/people")
	public ResponseEntity<CreatePersonStatus> createEntity(@Valid @RequestBody PersonDTO pVO) throws IllegalArgumentException, Exception {
		PersonDTO npVO = peopleService.savePerson(pVO);
		return ResponseEntity.ok(new CreatePersonStatus(true,npVO));
	}

	@PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    @PutMapping(value="/people/{id}")
    public ResponseEntity<CreatePersonStatus> updateEntity(@Valid @RequestBody PersonDTO pVO, @PathVariable Long id) throws IllegalArgumentException, Exception {
		if (pVO.getId().equals(id)) {
			PersonDTO npVO = peopleService.savePerson(pVO);
			return ResponseEntity.ok(new CreatePersonStatus(true,npVO));
		} else {
			throw ( new IllegalArgumentException() );
		}
    }


	@PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
	@DeleteMapping(value = "/people/{id}")
	public ResponseEntity<DeleteStatus> deleteEntity(@PathVariable Long id) {
		peopleService.deletePerson(id);
		return ResponseEntity.ok(new DeleteStatus(true,id));
    }


	/* Curren user REST service */


	@GetMapping(value="/currentuser")
    public ResponseEntity<UserDTO> loggedInUser(Principal principal) throws Exception {
			log.debug("loggedInUser... "+ principal);
			if (principal == null) {
				// Dummy for testing purposes
     			return ResponseEntity.ok(createDummyUser());
			} else {
    			return ResponseEntity.ok(securityService.getByUserName(principal.getName()));
			}

    }

	private UserDTO createDummyUser() {

		HashSet<UserRoles> roles = new HashSet<UserRoles>();
		roles.add(UserRoles.Staff);
		HashMap<String,String> depts = new HashMap<String,String>();
		depts.put(Integer.toString (Year.now().getValue()) , "IOB");

		UserDTO uvo = UserDTO.builder()
			.id(0L)
			.username("anonymous")
			.formName("Anonymous")
			.name("Anonymous")
			.userRoles(roles)
			.principalDepts( depts )
			.build();
		return uvo;
	}

}
