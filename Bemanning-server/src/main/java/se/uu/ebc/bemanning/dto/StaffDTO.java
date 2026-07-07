package se.uu.ebc.bemanning.dto;

import se.uu.ebc.bemanning.enums.EmploymentType;
import se.uu.ebc.bemanning.enums.StaffKind;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@Slf4j
public class StaffDTO {

    private Long id;

	private Long personId;
	private Long organisationUnitId;

    private String program;
    private Float percentGU;
    private Float hoursGU;
    
    private EmploymentType position;

    private Float hourlyCharge;
    private Float maxHourlyCharge;

    private String year;
    private String note;
    private Float ib;
    private Float ubLastYear;
    private Float accumulatedHours;

	private String name;
	private String formName;
	private String ouDesignation;

	private StaffKind staffKind;

}