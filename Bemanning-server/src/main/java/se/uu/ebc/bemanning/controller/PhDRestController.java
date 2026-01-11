package se.uu.ebc.bemanning.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import lombok.extern.slf4j.Slf4j;


import se.uu.ebc.bemanning.service.PhDService;
import se.uu.ebc.bemanning.vo.PhDPositionVO;
import se.uu.ebc.bemanning.vo.ProgressVO;

import java.util.List;

import jakarta.validation.Valid;
 
 
@RestController
@RequestMapping(value = "/rest")
@CrossOrigin(origins = "http://localhost:1841", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@Slf4j
public class PhDRestController {


	@Autowired
	PhDService phdService;

	/* PhDPositions */

	private record DeleteStatus (Boolean sucess, Long id) {}
	private record CreatePhdPositionStatus (Boolean sucess, PhDPositionVO phdpositions) {}
	private record CreatePhdPositionListStatus (Boolean success, List<PhDPositionVO> phdpositions) {};
	private record CreateProgressStatus (Boolean sucess, ProgressVO progress) {}
	private record CreateProgressListStatus (Boolean success, List<ProgressVO> progress) {};
			
			
    @GetMapping(value="/phdpositions")
    public ResponseEntity getAllPhdPositions() {
		return ResponseEntity.ok(new CreatePhdPositionListStatus (true, phdService.getAllPhDPositions() )); 
    }

    @GetMapping(value="/phdpositions/{id}")
    public PhDPositionVO getPhdPosition(@PathVariable Long id) {
		return phdService.getPhDById(id); 
    }
   
	@PreAuthorize("hasRole('ROLE_PHDADMIN')")
    @PostMapping(value="/phdpositions")
	public ResponseEntity createPhdPosition(@Valid @RequestBody PhDPositionVO pVO) throws Exception {
		PhDPositionVO npVO = phdService.savePhDPosition(pVO);
		return ResponseEntity.ok(new CreatePhdPositionStatus(true,npVO));
	}

	@PreAuthorize("hasRole('ROLE_PHDADMIN')")
    @PutMapping(value="/phdpositions/{id}")
    public ResponseEntity updatePhdPosition(@Valid @RequestBody PhDPositionVO pVO, @PathVariable Long id) throws Exception {
		if (pVO.getId().equals(id)) {
			PhDPositionVO npVO = phdService.savePhDPosition(pVO);
			return ResponseEntity.ok(new CreatePhdPositionStatus(true,npVO));		
		} else {
			throw ( new IllegalArgumentException() );
		} 			
    }


	@PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
	@DeleteMapping(value = "/phdpositions/{id}")
	public ResponseEntity deletePhdPosition(@PathVariable Long id) {
		phdService.deletePhDPosition(id);
		return ResponseEntity.ok(new DeleteStatus(true,id));
    }

	/* Progress */

    @GetMapping(value="/progress")
    public ResponseEntity getAllProgress() {
		return ResponseEntity.ok(new CreateProgressListStatus (true, phdService.getAllProgress() )); 
    }

	@PreAuthorize("hasRole('ROLE_PHDADMIN')")
    @PostMapping(value="/progress")
	public ResponseEntity createProgress(@Valid @RequestBody ProgressVO pVO) throws Exception {
		ProgressVO npVO = phdService.saveProgress(pVO);
		return ResponseEntity.ok(new CreateProgressStatus(true,npVO));
	}

	@PreAuthorize("hasRole('ROLE_PHDADMIN')")
    @PutMapping(value="/progress/{id}")
    public ResponseEntity updateProgress(@Valid @RequestBody ProgressVO pVO, @PathVariable Long id) throws Exception {
		if (pVO.getId().equals(id)) {
			ProgressVO npVO = phdService.saveProgress(pVO);
			return ResponseEntity.ok(new CreateProgressStatus(true,npVO));		
		} else {
			throw ( new IllegalArgumentException() );
		} 			
    }


	@PreAuthorize("hasRole('ROLE_PHDADMIN')")
	@DeleteMapping(value = "/progress/{id}")
	public ResponseEntity deleteProgress(@PathVariable Long id) {
		phdService.deleteProgress(id);
		return ResponseEntity.ok(new DeleteStatus(true,id));
    }

	
} 
