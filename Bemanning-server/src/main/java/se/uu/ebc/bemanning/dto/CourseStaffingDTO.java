package se.uu.ebc.bemanning.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;

import se.uu.ebc.bemanning.enums.CourseStaffingType;

import lombok.*;

import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@Slf4j
public class CourseStaffingDTO {

    private Long id;

    @NotBlank(message = "Type is mandatory")
    private CourseStaffingType type;
 
    private String note;    

    private Long staffId;
    private Long courseInstanceId;
    private String assigningDeptAbbreviation;

    private AssignmentDTO legacy;
    private AssignmentDTO plan;
    private AssignmentDTO te;
    private AssignmentDTO outcome;
	
	private LocalDateTime creationDate;
	private LocalDateTime lastModifiedDate;

}
