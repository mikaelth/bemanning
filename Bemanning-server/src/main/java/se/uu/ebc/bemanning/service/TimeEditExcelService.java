package se.uu.ebc.bemanning.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import se.uu.ebc.bemanning.enums.ActivityType;
import se.uu.ebc.bemanning.enums.TEColumnHeader;
import se.uu.ebc.bemanning.service.ColumnHeadersRecord;
import se.uu.ebc.bemanning.vo.TEExcelVO;
import se.uu.ebc.bemanning.repo.TEActivityRepo;
import se.uu.ebc.bemanning.entity.utils.TEActivity;

import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.annotation.PostConstruct;

import org.modelmapper.ModelMapper;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;

@Slf4j
@ConfigurationPropertiesScan 
@Service
public class TimeEditExcelService {

    @Value("${bemanning.upload.tefile}")
    private String excelFilePath;

    private final ColumnHeadersRecord colHeaders;
    private final TEActivityRepo teActivityRepo;

 	/* Constructor injection */
   public TimeEditExcelService (ColumnHeadersRecord colHeaders, TEActivityRepo teActivityRepo) {
        this.colHeaders = colHeaders;
		this.teActivityRepo = teActivityRepo;
    }
	public record EntryRecord (String staff, String course, String activity, Float hours) {
		public String getKey() {
			return staff+";"+course+";"+activity;
		}
	};
	
// 	@PostConstruct
// 	public void init () {
// 		log.debug("Header for activity: {}",colHeaders.seActivity());
// 	}

 		
    public List<TEExcelVO> getExcelDataAsList(boolean substitutingExistingValues) throws IOException {
        List<TEExcelVO> teEntries = new ArrayList<TEExcelVO>();
        List<EntryRecord> entryList = new ArrayList<EntryRecord>();
        
 		log.debug("In getExcelDataAsList");

		Map<String,Integer> headerMap = new HashMap<String,Integer>();
		Map<String,Integer> columnMap = new HashMap<String, Integer>();

		Map<String,Float> sumMap = new HashMap<String,Float>();
		Map<String,EntryRecord> entryMap = new HashMap<String,EntryRecord>();
				
        try (Workbook workbook = WorkbookFactory.create(new File(excelFilePath))) {
            Sheet sheet = workbook.getSheetAt(0);
			log.debug("Excel file opened {}", sheet.getSheetName());

			
			Row row; 
			Cell cell;
			int rowNum = 1;
 			int colNum = 0;
 			while (headerMap.size() < 4) {
				row = sheet.getRow(rowNum++);
                while (row == null) continue;
	
				for (colNum = 0; row.getCell(colNum) != null; colNum++)	{
					cell = row.getCell(colNum);
					columnMap.put(cell.getStringCellValue(),colNum);	

					log.debug("Row {} and column {}, value {}",rowNum,colNum,cell.getStringCellValue());
			
				}
				
				for (String item : colHeaders.asList())	{
					if (columnMap.containsKey(item)) {
						headerMap.put(item, columnMap.get(item));
					}
				}	
				
 			}
 	
 			log.debug("The column map is {}",columnMap);
 			log.debug("The header map is {}",headerMap);
 
//			List<String> headers = headerMap.containsKey(colHeaders.seActivity()) ? colHeaders.seList() colHeaders.enList();
			int stfIdx;
			int crsIdx;
			int actIdx;
			int timIdx;

			if (headerMap.containsKey(colHeaders.seStaff())) {
				stfIdx = headerMap.get(colHeaders.seStaff());
				crsIdx = headerMap.get(colHeaders.seCourse());
				actIdx = headerMap.get(colHeaders.seActivity());
				timIdx = headerMap.get(colHeaders.seTime());
			
			} else {
				stfIdx = headerMap.get(colHeaders.enStaff());
				crsIdx = headerMap.get(colHeaders.enCourse());
				actIdx = headerMap.get(colHeaders.enActivity());
				timIdx = headerMap.get(colHeaders.enTime());
			}
					
			
            for (int i = rowNum; i <= sheet.getLastRowNum(); i++) {
                row = sheet.getRow(i);
                if (row == null) continue;
                String stfVal = row.getCell(stfIdx).getStringCellValue();
                String crsVal = row.getCell(crsIdx).getStringCellValue();
                String actVal = row.getCell(actIdx).getStringCellValue();
                float timVal = (float)Math.ceil(row.getCell(timIdx).getNumericCellValue());
                
				if (stfVal.equals("") || 
					crsVal.equals("") ||
					actVal.equals("")) continue;
					
					for (String s : stfVal.split(", ")) {
						EntryRecord entRec = new EntryRecord (s, crsVal,actVal, timVal);
 						entryMap.put(entRec.getKey(),entRec);
 						if (sumMap.containsKey(entRec.getKey())) {
 							sumMap.put( entRec.getKey(), sumMap.get(entRec.getKey()) + entRec.hours() );
 						} else {
 							sumMap.put(entRec.getKey(),entRec.hours());
 						}
					 	log.debug("{}, {}, {}, {}, {}, key {}", s, crsVal,actVal, timVal, teActivityRepo.findByTeText(actVal).orElse(new TEActivity()).getBpActivity(), entRec.getKey());
					}

			}
 		}
		
		log.debug("The sumMap {}", sumMap);
		
		for (String theKey : entryMap.keySet()) {
			TEExcelVO tVO = new TEExcelVO().builder()
				.activity(entryMap.get(theKey).activity())
				.activityType(teActivityRepo.findByTeText(entryMap.get(theKey).activity()).orElse(new TEActivity()).getBpActivity())
				.staff(entryMap.get(theKey).staff())
				.duration(sumMap.get(theKey))
				.courseCode(entryMap.get(theKey).course().split("-")[0])
				.ciNumber(entryMap.get(theKey).course().split("-")[2])
				.build();
			teEntries.add(tVO);
			log.debug("Entry {}",tVO);
		}
		

        return teEntries;
    }
    
}