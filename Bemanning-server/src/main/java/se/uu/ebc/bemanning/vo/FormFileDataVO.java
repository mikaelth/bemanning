package se.uu.ebc.bemanning.vo;

import org.springframework.web.multipart.MultipartFile;
import lombok.*;

import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@Slf4j
public class FormFileDataVO {

	private Integer year;
	private MultipartFile excelFile;
	private boolean ignoreExistingValues;
	
}

