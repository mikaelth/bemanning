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
import se.uu.ebc.bemanning.dto.UserDTO;
import se.uu.ebc.bemanning.dto.YearlyStaffingDTO;
import se.uu.ebc.bemanning.enums.UserRoles;
import se.uu.ebc.bemanning.service.YearlyStaffingService;

import java.util.List;
import java.util.HashSet;
import java.util.HashMap;
import java.time.Year;

import jakarta.validation.Valid;


@RestController
@RequestMapping(value = "/rest")
@CrossOrigin(origins = "http://localhost:1841")
@Slf4j
public class YearlyStaffingRestController {



    // Spring automatically injects this dependency!
	private final YearlyStaffingService yearlyStaffingService;

	public YearlyStaffingRestController(SecurityService securityService, YearlyStaffingService yearlyStaffingService) {
		this.yearlyStaffingService = yearlyStaffingService;
	}

	private record DeleteStatus (Boolean sucess, Long id) {}
	private record CreateYearlyStaffingStatus (Boolean success, YearlyStaffingDTO yearlyStaffing) {}
	private record YearlyStaffing (List<YearlyStaffingDTO> yearlyStaffing) {};


	/* Yearly Staffing plans */	

    @GetMapping(value="/ysp")
    public ResponseEntity<YearlyStaffing> getAllEntities() {
		return ResponseEntity.ok(new YearlyStaffing (yearlyStaffingService.getAllYearlyStaffings() ));
    }

    @GetMapping(value="/ysp/{id}")
    public YearlyStaffingDTO getEntity(@PathVariable Long id) {
		return yearlyStaffingService.getById(id);
    }

	@PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    @PostMapping(value="/ysp")
	public ResponseEntity<CreateYearlyStaffingStatus> createEntity(@Valid @RequestBody YearlyStaffingDTO pVO) throws Exception {
		YearlyStaffingDTO npVO = yearlyStaffingService.saveYearlyStaffing(pVO);
		return ResponseEntity.ok(new CreateYearlyStaffingStatus(true,npVO));
	}

	@PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    @PutMapping(value="/ysp/{id}")
    public ResponseEntity<CreateYearlyStaffingStatus> updateEntity(@Valid @RequestBody YearlyStaffingDTO pVO, @PathVariable Long id) throws Exception {
		if (pVO.getId().equals(id)) {
			YearlyStaffingDTO npVO = yearlyStaffingService.saveYearlyStaffing(pVO);
			return ResponseEntity.ok(new CreateYearlyStaffingStatus(true,npVO));
		} else {
			throw ( new IllegalArgumentException() );
		}
    }


	@PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
	@DeleteMapping(value = "/ysp/{id}")
	public ResponseEntity<DeleteStatus> deleteEntity(@PathVariable Long id) {
		yearlyStaffingService.deleteYearlyStaffing(id);
		return ResponseEntity.ok(new DeleteStatus(true,id));
    }



}
