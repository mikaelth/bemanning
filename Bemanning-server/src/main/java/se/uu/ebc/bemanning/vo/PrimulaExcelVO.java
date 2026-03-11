package se.uu.ebc.bemanning.vo;

import java.util.List;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class PrimulaExcelVO {

	private String employeeNumber;
	private String year;
	private Float hourlyCost;

}

