package se.uu.ebc.bemanning.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import se.uu.ebc.bemanning.service.TimeEditExcelService;
import se.uu.ebc.bemanning.vo.FormFileDataVO;
import se.uu.ebc.bemanning.vo.TEExcelVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/files/te")
public class TEFileController {

    @Value("${bemanning.upload.dir}")
    private String uploadDir;

    @Value("${bemanning.upload.tefile}")
    private String excelFilePath;

    @Autowired
    private TimeEditExcelService teExcelService;

/*
    @PostMapping("/rest/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        Path path = Paths.get(uploadDir + File.separator + file.getOriginalFilename());
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());
        return "File uploaded successfully: " + file.getOriginalFilename();
    }

 
    @PostMapping("/save")
    public String saveData() throws IOException {
        List<Invoice> invoices = excelDataService.getExcelDataAsList();
        invoiceRepository.saveAll(invoices);
        return invoices.size() + " records saved to database.";
    }
 */
    
    
    
	@Secured({("ROLE_DIRECTOROFSTUDIES")})
	@RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String viewCSVCIUploadRequest(Model model, HttpServletRequest request) {
		try {
			FormFileDataVO fb = new FormFileDataVO();
			fb.setIgnoreExistingValues(false);

//			model.addAttribute("years",edRepo.getYears());
 			model.addAttribute("formValues",fb);

			return "ViewTEExcelUpload";

        } catch (Exception e) {
				log.error("viewCSVCIUploadRequest, pesky exception "+e);
           return "{\"ERROR\":"+e.getMessage()+"\"}";
        }
	}

	@Secured({("ROLE_DIRECTOROFSTUDIES")})
	@RequestMapping(value="/bulk/upload", method = RequestMethod.POST, headers = "Accept=application/json")
    public String requestUpdateRegsFromCSV(Model model, HttpServletRequest request, HttpServletResponse response, final FormFileDataVO formValues) throws IOException{

		log.debug("FormValues: {}",formValues);
//        Path path = Paths.get(uploadDir + File.separator + formValues.getExcelFile().getOriginalFilename());
        Path path = Paths.get(excelFilePath);
		Files.createDirectories(path.getParent());
        Files.write(path, formValues.getExcelFile().getBytes());
		
 		model.addAttribute( "teEntries",teExcelService.getExcelDataAsList(formValues.isIgnoreExistingValues()).stream().sorted( (TEExcelVO t1,TEExcelVO t2) -> t1.getStaff().compareTo(t2.getStaff()) ) );
		log.debug("Model: {}",model);

        return "TEBulkUpload";
    }

}