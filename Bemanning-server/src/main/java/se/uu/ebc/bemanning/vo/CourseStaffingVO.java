package se.uu.ebc.bemanning.vo;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;

import se.uu.ebc.bemanning.enums.CourseStaffingType;

import lombok.*;

import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@Slf4j
public class CourseStaffingVO {

    private Long id;

    @NotBlank(message = "Type is mandatory")
    private CourseStaffingType type;
 
    private String note;    

    private Long staffId;
    private Long courseInstanceId;
    private String assigningDeptAbbreviation;

    private AssignmentVO legacy;
    private AssignmentVO plan;
    private AssignmentVO te;
    private AssignmentVO outcome;
	
	private LocalDateTime creationDate;
	private LocalDateTime lastModifiedDate;

}
