package se.uu.ebc.bemanning.vo;

import se.uu.ebc.bemanning.enums.AssignmentType;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@Slf4j
public class AssignmentVO {
	    
    private Long id;

    @NotBlank(message = "Type is mandatory")
    private AssignmentType type;

	private Long staffId;
	private Long courseInstanceId;
	private String assigningDept;

    private Float hoursAdmin;
    private Float hoursDevelopment;
    private Float hoursLecture;
    private Float hoursPractical;
    private Float hoursExcursion;
    private Float hoursSeminar;

    private String note;

	private String year;
    private Float totalHours;

	private LocalDateTime creationDate;
	private LocalDateTime lastModifiedDate;
	

}
