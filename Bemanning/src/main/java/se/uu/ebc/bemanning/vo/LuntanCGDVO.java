package se.uu.ebc.bemanning.vo;

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import java.util.stream.Collectors;
import static java.util.stream.Collectors.*;

import lombok.extern.slf4j.Slf4j;
import lombok.Data;
import lombok.NoArgsConstructor;

@Slf4j
@Data
@NoArgsConstructor
public class LuntanCGDVO {


    private Long id;

	private boolean locked;
	private boolean supplement;

	private String ciDesignation;

	private Long courseId;
	private String courseGroup;
	private String courseDesignation;
	private String courseLeader;

	private Long economyDocId;
	private String extraDesignation;
	private String instanceCode;

	private boolean registrationValid;
	private Integer registeredStudents;
	private Integer startRegStudents;
	private Integer modelStudentNumber;

    private String note;
	private boolean firstInstance;

    private Map<String,Float> grantDistribution;
    private Map<String,Float> grantsDistributed;


 	/* Setters and getters */


    /* Public methods */


 	/* Constructors */


}
