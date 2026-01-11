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

import java.util.List;

import jakarta.validation.Valid;
 
/* 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.multipart.commons.CommonsMultipartFile;
 
import se.uu.ebc.bemanning.security.UserRepo;
import se.uu.ebc.bemanning.repo.PhDPositionRepo;
import se.uu.ebc.bemanning.entity.PhDPosition;
import se.uu.ebc.bemanning.service.PhDService;
import se.uu.ebc.bemanning.vo.PhDPositionVO;
import se.uu.ebc.bemanning.vo.ProgressVO;
import se.uu.ebc.bemanning.vo.UserVO;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import se.uu.ebc.bemanning.util.DateNullTransformer;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

 */
 
@RestController
@RequestMapping(value = "/rest")
@CrossOrigin(origins = "http://localhost:1841", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@Slf4j
public class PhDRestController {


	@Autowired
	PhDService phdService;

	/* PhDPositions */

	private record DeleteStatus (Boolean sucess, Long id) {}
	private record CreatePhdpositionStatus (Boolean sucess, PhDPositionVO phdpositions) {}
	private record CreatePhdpositionListStatus (Boolean success, List<PhDPositionVO> phdpositions) {};
			
    @GetMapping(value="/phdpositions")
    public ResponseEntity getAllEntities() {
		return ResponseEntity.ok(new CreatePhdpositionListStatus (true, phdService.getAllPhDPositions() )); 
    }

    @GetMapping(value="/phdpositions/{id}")
    public PhDPositionVO getEntity(@PathVariable Long id) {
		return phdService.getPhDById(id); 
    }
   
	@PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    @PostMapping(value="/phdpositions")
	public ResponseEntity createEntity(@Valid @RequestBody PhDPositionVO pVO) throws Exception {
		PhDPositionVO npVO = phdService.savePhDPosition(pVO);
		return ResponseEntity.ok(new CreatePhdpositionStatus(true,npVO));
	}

	@PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
    @PutMapping(value="/phdpositions/{id}")
    public ResponseEntity updateEntity(@Valid @RequestBody PhDPositionVO pVO, @PathVariable Long id) throws Exception {
		if (pVO.getId().equals(id)) {
			PhDPositionVO npVO = phdService.savePhDPosition(pVO);
			return ResponseEntity.ok(new CreatePhdpositionStatus(true,npVO));		
		} else {
			throw ( new IllegalArgumentException() );
		} 			
    }


	@PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
	@DeleteMapping(value = "/phdpositions/{id}")
	public ResponseEntity deleteEntity(@PathVariable Long id) {
		phdService.deletePhDPosition(id);
		return ResponseEntity.ok(new DeleteStatus(true,id));
    }

		
/* 
    @RequestMapping(value="/phdpositions", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> allPeople() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        try {
 			return new ResponseEntity<String>(new JSONSerializer().prettyPrint(true).exclude("*.class").rootName("phdpositions").transform(new DateNullTransformer("yyyy-MM-dd"), Date.class).serialize(phdService.getAllPhDPositions()), headers, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
  	
    }
 
	@PreAuthorize("hasRole('ROLE_PHDADMIN')")
    @RequestMapping(value="/phdpositions/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updatePhDPosition(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
			logger.debug("updatePhDPosition, json "+ json);
			PhDPositionVO pVO = new JSONDeserializer<PhDPositionVO>().use(null, PhDPositionVO.class).use(Date.class, new DateNullTransformer("yyyy-MM-dd") ).deserialize(json);
			logger.debug("updatePhDPosition, pVO "+ ReflectionToStringBuilder.toString(pVO, ToStringStyle.MULTI_LINE_STYLE));

			pVO.setId(id);
			pVO = phdService.savePhDPosition(pVO);
			
 			String restResponse = new JSONSerializer().prettyPrint(true).exclude("*.class").rootName("phdpositions").transform(new DateNullTransformer("yyyy-MM-dd"), Date.class).serialize(pVO);
			restResponse = new StringBuilder(restResponse).insert(1, "success: true,").toString();

            return new ResponseEntity<String>(restResponse, headers, HttpStatus.OK);
        } catch (Exception e) {
			logger.error("updatePhDPosition got a pesky exception: "+ e + e.getCause());
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

 
	@PreAuthorize("hasRole('ROLE_PHDADMIN')")
    @RequestMapping(value="/phdpositions", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createPhDPosition(@RequestBody String json, UriComponentsBuilder uriBuilder) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
			PhDPositionVO pVO = new JSONDeserializer<PhDPositionVO>().use(null, PhDPositionVO.class).use(Date.class, new DateNullTransformer("yyyy-MM-dd") ).deserialize(json);
			pVO = phdService.savePhDPosition(pVO);
            RequestMapping a = (RequestMapping) getClass().getAnnotation(RequestMapping.class);
            headers.add("Location",uriBuilder.path(a.value()[0]+"/"+pVO.getId().toString()).build().toUriString());

 			String restResponse = new JSONSerializer().prettyPrint(true).exclude("*.class").rootName("phdpositions").transform(new DateNullTransformer("yyyy-MM-dd"), Date.class).serialize(pVO);
			restResponse = new StringBuilder(restResponse).insert(1, "success: true,").toString();

            return new ResponseEntity<String>(restResponse, headers, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


//	@PreAuthorize("hasRole('ROLE_PHDADMIN')")
	@PreAuthorize("hasRole('ROLE_COREDATAADMIN')")
	@RequestMapping(value = "/phdpositions/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
	public ResponseEntity<String> deletePhDPosition(@PathVariable("id") Long id) {
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
			phdService.deletePhDPosition(id);
            return new ResponseEntity<String>("{success: true, id : " +id.toString() + "}", headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
 */


	/* Progress */
		
/* 
    @RequestMapping(value="/progress", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> allProgress() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        try {
 			return new ResponseEntity<String>(new JSONSerializer().prettyPrint(true).exclude("*.class").rootName("progress").transform(new DateNullTransformer("yyyy-MM-dd"), Date.class).serialize(phdService.getAllProgress()), headers, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
  	
    }
 
	@PreAuthorize("hasRole('ROLE_PHDADMIN')")
    @RequestMapping(value="/progress/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateProgress(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
			logger.debug("updateProgress, json "+ json);
			ProgressVO sVO = new JSONDeserializer<ProgressVO>().use(null, ProgressVO.class).use(Date.class, new DateNullTransformer("yyyy-MM-dd") ).deserialize(json);
			logger.debug("updateProgress, sVO "+ ReflectionToStringBuilder.toString(sVO, ToStringStyle.MULTI_LINE_STYLE));

			sVO.setId(id);
			sVO = phdService.saveProgress(sVO);
			
 			String restResponse = new JSONSerializer().prettyPrint(true).exclude("*.class").rootName("progress").transform(new DateNullTransformer("yyyy-MM-dd"), Date.class).serialize(sVO);
			restResponse = new StringBuilder(restResponse).insert(1, "success: true,").toString();

            return new ResponseEntity<String>(restResponse, headers, HttpStatus.OK);
        } catch (Exception e) {
			logger.error("updateProgress got a pesky exception: "+ e + e.getCause());
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

 
	@PreAuthorize("hasRole('ROLE_PHDADMIN')")
    @RequestMapping(value="/progress", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createProgress(@RequestBody String json, UriComponentsBuilder uriBuilder) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
			ProgressVO sVO = new JSONDeserializer<ProgressVO>().use(null, ProgressVO.class).use(Date.class, new DateNullTransformer("yyyy-MM-dd") ).deserialize(json);
			sVO = phdService.saveProgress(sVO);
            RequestMapping a = (RequestMapping) getClass().getAnnotation(RequestMapping.class);
            headers.add("Location",uriBuilder.path(a.value()[0]+"/"+sVO.getId().toString()).build().toUriString());

 			String restResponse = new JSONSerializer().prettyPrint(true).exclude("*.class").rootName("progress").transform(new DateNullTransformer("yyyy-MM-dd"), Date.class).serialize(sVO);
			restResponse = new StringBuilder(restResponse).insert(1, "success: true,").toString();

            return new ResponseEntity<String>(restResponse, headers, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


	@PreAuthorize("hasRole('ROLE_PHDADMIN')")
	@RequestMapping(value = "/progress/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
	public ResponseEntity<String> deleteProgress(@PathVariable("id") Long id) {
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
			phdService.deleteProgress(id);
            return new ResponseEntity<String>("{success: true, id : " +id.toString() + "}", headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

 */
	
} 
