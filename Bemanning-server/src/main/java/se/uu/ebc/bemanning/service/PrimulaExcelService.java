package se.uu.ebc.bemanning.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

import se.uu.ebc.bemanning.enums.ActivityType;
import se.uu.ebc.bemanning.enums.TEMatchStatus;
//import se.uu.ebc.bemanning.enums.TEColumnHeader;
import se.uu.ebc.bemanning.service.ColumnHeadersRecord;
import se.uu.ebc.bemanning.service.CourseStaffingService;
import se.uu.ebc.bemanning.service.AKKAService;
import se.uu.ebc.bemanning.repo.TEActivityRepo;
import se.uu.ebc.bemanning.repo.CourseStaffingRepo;
import se.uu.ebc.bemanning.repo.StaffRepo;
import se.uu.ebc.bemanning.repo.AkkaStaffRepo;
import se.uu.ebc.bemanning.repo.CourseInstanceRepo;
import se.uu.ebc.bemanning.entity.utils.TEActivity;
import se.uu.ebc.bemanning.dto.PrimulaEntriesExcel;
import se.uu.ebc.bemanning.entity.assignment.CourseStaffingModern;
import se.uu.ebc.bemanning.entity.courseinstance.CourseInstance;
import se.uu.ebc.bemanning.entity.staff.Staff;

import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.annotation.PostConstruct;

import com.poiji.bind.Poiji;

import org.modelmapper.ModelMapper;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;

@Slf4j
@ConfigurationPropertiesScan
@Service
public class PrimulaExcelService {

    @Value("${bemanning.upload.primulafile}")
    private String excelFilePath;

    private final AKKAService akkaService;
    private final AkkaStaffRepo akkaStaffRepo;

    /* Constructor injection of autowired */
    PrimulaExcelService (AKKAService akkaService, AkkaStaffRepo akkaStaffRepo) {
    	this.akkaService = akkaService;
    	this.akkaStaffRepo = akkaStaffRepo;
    }

    public List<PrimulaEntriesExcel> getExcelDataAsList (boolean overWrite) {
		Map<String,PrimulaEntriesExcel> pMap = new HashMap<String,PrimulaEntriesExcel>();
		File file = new File(excelFilePath);
        List<PrimulaEntriesExcel> entries = Poiji.fromExcel(file, PrimulaEntriesExcel.class);

//        entries.forEach(entry -> {updateAkkaCost(entry, "2026");});
        entries.forEach( entry -> {pMap.put(entry.getPNIN(),entry);} );
		pMap.values().forEach(entry -> {updateAkkaCost(entry, "2026");});

        return entries;
    }

/*
	private void updateAkkaCost (PrimulaEntriesExcel entry, String year) {

		try {
			for (String eNum : akkaService.findEmployeenumberBypNIN(entry.pNINForLdap())) {
				akkaStaffRepo.findUserByEmployeeNumberAndYear(eNum, year).ifPresent (
					staff -> {
						staff.setHourlyCharge (entry.hourlyCost());
						akkaStaffRepo.save(staff);
						log.debug("With {}, {}, updated {} to {}",eNum, entry.pNINForLdap(), staff.getPerson().getName(),staff.getHourlyCharge());
						entry.setUpdated(true);
					}
				);
			}
		} catch (Exception e) {
		}
	}
 */

	private void updateAkkaCost (PrimulaEntriesExcel entry, String year) {

		try {
			for (String eNum : akkaService.findUsernameBypNIN(entry.pNINForLdap())) {
				akkaStaffRepo.findStaffByUsernameAndYear(eNum, year).ifPresent (
					staff -> {
						staff.setHourlyCharge (entry.hourlyCost());
						akkaStaffRepo.save(staff);
						log.debug("With {}, {}, updated {} to {}",eNum, entry.pNINForLdap(), staff.getPerson().getName(),staff.getHourlyCharge());
						entry.setUpdated(true);
					}
				);
			}
		} catch (Exception e) {
		}
	}

}
