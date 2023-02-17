package se.uu.ebc.bemanning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import java.io.Serializable;

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import javax.annotation.Generated;

import se.uu.ebc.bemanning.entity.CourseInstance;
import se.uu.ebc.bemanning.repo.CourseInstanceRepo;
import se.uu.ebc.bemanning.vo.LuntanCGDVO;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LuntanService {

	@Autowired
	CourseInstanceRepo ciRepo;

	RestTemplate restTemplate = new RestTemplate();
	String luntanCgdURL = "http://localhost:8080/bemanning/cgd?year=";

	public List<LuntanCGDVO>  getLuntanCGD (Integer year) {
		LuntanCGDVO[] entries = restTemplate.getForObject(luntanCgdURL+year, LuntanCGDVO[].class);
		for (LuntanCGDVO ci : entries) {
			String cic = ci.getCourseDesignation().substring(0,6);
			log.debug(cic + ", "+ciRepo.findByYearAndCourseCode(Integer.toString(year),cic).toString());
		}
		return Arrays.asList(entries);
	}


}
