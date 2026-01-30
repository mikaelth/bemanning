package se.uu.ebc.bemanning.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
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

import se.uu.ebc.bemanning.repo.PersonRepo;
import se.uu.ebc.bemanning.security.SecurityService;
import se.uu.ebc.bemanning.enums.UserRoleType;
import se.uu.ebc.bemanning.service.PeopleService;
import se.uu.ebc.bemanning.vo.PersonVO;
import se.uu.ebc.bemanning.vo.UserVO;

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


	@Autowired
	SecurityService securityService;

	@Autowired
	PeopleService peopleService;


	private record DeleteStatus (Boolean sucess, Long id) {}
	private record CreatePersonStatus (Boolean sucess, PersonVO people) {}
	private record People (List<PersonVO> people) {};


	/* Persons */

    @GetMapping(value="/people")
    public ResponseEntity<People> getAllEntities() {
		return ResponseEntity.ok(new People (peopleService.getAllPersons() ));
    }

    @GetMapping(value="/people/{id}")
    public PersonVO getEntity(@PathVariable Long id) {
		return peopleService.getById(id);
    }

	@PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    @PostMapping(value="/people")
	public ResponseEntity<CreatePersonStatus> createEntity(@Valid @RequestBody PersonVO pVO) throws Exception {
		PersonVO npVO = peopleService.savePerson(pVO);
		return ResponseEntity.ok(new CreatePersonStatus(true,npVO));
	}

	@PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    @PutMapping(value="/people/{id}")
    public ResponseEntity<CreatePersonStatus> updateEntity(@Valid @RequestBody PersonVO pVO, @PathVariable Long id) throws Exception {
		if (pVO.getId().equals(id)) {
			PersonVO npVO = peopleService.savePerson(pVO);
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
    public ResponseEntity<UserVO> loggedInUser(Principal principal) throws Exception {
			log.debug("loggedInUser... "+ principal);
			if (principal == null) {
				// Dummy for testing purposes
     			return ResponseEntity.ok(createDummyUser());
			} else {
    			return ResponseEntity.ok(securityService.getByUserName(principal.getName()));
			}

    }

	private UserVO createDummyUser() {

		HashSet<UserRoleType> roles = new HashSet<UserRoleType>();
		roles.add(UserRoleType.Staff);
		HashMap<String,String> depts = new HashMap<String,String>();
		depts.put(Integer.toString (Year.now().getValue()) , "IOB");

		UserVO uvo = UserVO.builder()
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
