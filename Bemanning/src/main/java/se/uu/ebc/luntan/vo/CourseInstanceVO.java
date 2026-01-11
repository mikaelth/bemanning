package se.uu.ebc.luntan.vo;

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import jakarta.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;
import lombok.Data;
import lombok.NoArgsConstructor;

@Slf4j
@Data
@NoArgsConstructor
public class CourseInstanceVO {

	 
    @NotNull
    private Long id;

	private boolean locked; 
	private boolean supplement; 
	
	private String ciDesignation;
	
	private Long courseId;
	private String courseGroup;
	private String courseDesignation;
	private String courseLeader;
	
	private Long preceedingCIId;
	private Long economyDocId;
	private Long balancedEconomyDocId;
	private Long fundingModelId;

	private String extraDesignation;
	private String instanceCode;

	private boolean registrationValid;
	private Integer registeredStudents;
	private Integer startRegStudents;
	private Integer modelStudentNumber;
	private String modelCase;
	
    private String note;
	private boolean balanceRequest = false;
	private boolean firstInstance;
	private boolean bookendOnly;
	
//	private Set<String> examiners;
//	private List<Examiner> examiners;

    private Map<String,Float> grantDistribution;
	
	private boolean individualYearlyCourse;
	
 	   

}
