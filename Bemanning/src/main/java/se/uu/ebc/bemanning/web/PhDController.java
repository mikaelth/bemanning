package se.uu.ebc.bemanning.web;
 
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

@Controller
@RequestMapping(value = "/rest")
@CrossOrigin(origins = "http://localhost:1841", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class PhDController {

    private Logger logger = Logger.getLogger(PhDController.class.getName());

/* 
	@Autowired
	PhDPositionRepo personRepo;
 */

	@Autowired
	PhDService phdService;

	/* PhDPositions */
		
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


	/* Progress */
		
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

	
} 
