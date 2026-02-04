package se.uu.ebc.bemanning.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@Slf4j
public class AssignmentVO {
	    
    private Long id;
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

	

}
