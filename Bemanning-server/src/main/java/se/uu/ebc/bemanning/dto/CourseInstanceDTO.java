package  se.uu.ebc.bemanning.dto;

import java.time.LocalDateTime;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@Slf4j
public class CourseInstanceDTO {

    private Long id;
    private String year;
    private String extraDesignation;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String note;
    private Integer numberOfStudents;
	private Long courseId;
	private Long courseLeaderId;

	private Long primaryExaminerId;
	private Long secondaryExaminerId;

	private String courseName;
	private String courseLeaderName;
	private String courseGroup;


	
}
