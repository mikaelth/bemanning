package se.uu.ebc.bemanning.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;

import lombok.extern.slf4j.Slf4j;

import se.uu.ebc.bemanning.service.OrgHierarchyService;
import se.uu.ebc.bemanning.service.TestService;

import java.io.IOException;


@RestController
@RequestMapping(value = "/rest")
@CrossOrigin(origins = "http://localhost:1841")
@Slf4j
public class TestController {


	
  @Autowired
	OrgHierarchyService ouService;

  @Autowired
	TestService testService;



    @GetMapping(value="/test")
    public ResponseEntity getAllEntities() throws IOException{
		return ResponseEntity.ok(ouService.getOUs() );
    }

   @GetMapping(value="/testldap")
    public ResponseEntity doTest() throws Exception{
		return ResponseEntity.ok(testService.test() );
    }


}
