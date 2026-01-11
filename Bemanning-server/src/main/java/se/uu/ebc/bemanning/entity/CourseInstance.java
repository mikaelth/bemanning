package se.uu.ebc.bemanning.entity;

import java.time.LocalDateTime;
import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "COURSE_INSTANCE")
@Getter
@Setter
//@Builder(toBuilder = true)
//@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode(callSuper = true)
@Slf4j
public class CourseInstance  extends Auditable {
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "COURSE_FK")
	private Course course;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "COURSE_LEADER_FK")
	private Staff courseLeader;


    
    @OneToMany(mappedBy = "courseInstance")
    private Set<CourseStaffing> assignments;
    
    @OneToMany(mappedBy = "courseInstance")
    private Set<CourseGrant> courseGrants;
    
    @Column(name = "YEAR", length = 255)
    @NotNull
    private String year;
    
    @Column(name = "EXTRA_DESIGNATION", length = 255)
    private String extraDesignation;
    
    @Column(name = "START_DATE")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;
    
    @Column(name = "END_DATE")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;
    
    @Column(name = "INCOME_GRANT")
    private Integer incomeGrant;
    
 /* 
   @Column(name = "ASSET_DISTRIBUTION_EB", precision = 12)
    @NotNull
    private Float assetDistributionEb;
    
    @Column(name = "ASSET_DISTRIBUTION_FB", precision = 12)
    @NotNull
    private Float assetDistributionFb;
    
    @Column(name = "ASSET_DISTRIBUTION_ME", precision = 12)
    @NotNull
    private Float assetDistributionMe;
    
    @Column(name = "ASSET_DISTRIBUTION_SB", precision = 12)
    @NotNull
    private Float assetDistributionSb;
    
    @Column(name = "ASSET_DISTRIBUTION_EFG", precision = 12)
    @NotNull
    private Float assetDistributionEfg;
    
    @Column(name = "ASSET_DISTRIBUTION_IEGS", precision = 12)
    @NotNull
    private Float assetDistributionIegs;
 */
    
    @Column(name = "NOTE", length = 255)
    private String note;
    
/* 
    @Column(name = "INCOME_ADJUSTMENT")
    private Integer incomeAdjustment;
    
    @Column(name = "REQUIRED_EFFORT_ADMIN", precision = 12)
    private Float requiredEffortAdmin;
    
    @Column(name = "REQUIRED_EFFORT_DEVELOPMENT", precision = 12)
    private Float requiredEffortDevelopment;
    
    @Column(name = "REQUIRED_EFFORT_LECTURE", precision = 12)
    private Float requiredEffortLecture;
    
    @Column(name = "REQUIRED_EFFORT_PRACTICAL", precision = 12)
    private Float requiredEffortPractical;
    
    @Column(name = "REQUIRED_EFFORT_EXCURSION", precision = 12)
    private Float requiredEffortExcursion;
    
    @Column(name = "REQUIRED_EFFORT_SEMINAR", precision = 12)
    private Float requiredEffortSeminar;
    
    @Column(name = "PERCENT_COURSE", precision = 12)
    private Float percentCourse;
    
    @Column(name = "NUMBER_OF_STUDENTS")
    private Integer numberOfStudents;
    
    @Column(name = "TOTAL_GRANT")
    private Integer totalGrant;
 */
    
    @Column(name = "OLD_ID")
    private Long oldId;
 
 
	@ManyToOne
    @JoinColumn(name = "PRIM_EXAM_FK")
	private Staff primaryExaminer;

	@ManyToOne
    @JoinColumn(name = "SEC_EXAM_FK")
	private Staff secondaryExaminer;

 

	@OneToMany(mappedBy = "courseInstance")
	Set<InstanceDeptYear> yearlyStaffings;




	/* Public methods */
	 
/* 
	public String getDesignation() {
		return course.getCode() +" " + course.getSeName() + " " + extraDesignation;
	}

	public int getTotalHours() {
		int hours = 0;
		for (Assignment asn : this.assignments) {
			hours += asn.getTotalHours();
		}
		return hours;
	}

	public float getTotalAssignmentCost() {
		float cost = 0.0f;
		for (Assignment asn : this.assignments) {
			cost += asn.getAssignmentCost();
		}
		return cost;
	}

	public float getTotalAssignmentCost(OrganisationUnit ou) {
		float cost = 0.0f;

		for (Assignment asn : this.assignments) {
			cost += (ou == asn.getAssigningDept()) ? asn.getAssignmentCost() : 0.0f;
		}
		return cost;
	}

	public float getTotalGrants() {
		float income = 0.0f;
		for (CourseGrant grant : this.courseGrants) {
			income += (grant.getType().includeInSummary()) ? grant.getAmount() : 0.0f;
		}
		return income;
	}

	public float getTotalGrants(OrganisationUnit ou) {
		float income = 0.0f;
		for (CourseGrant grant : this.courseGrants) {
			income += (ou == grant.getDepartment() && grant.getType().includeInSummary()) ? grant.getAmount() : 0.0f;
		}
		return income;
	}
	
 */


	/* Constructors */
	
/* 
	public CourseInstance() {
		assetDistributionEb= 0.0f;
		assetDistributionFb= 0.0f;
		assetDistributionMe= 0.0f;
		assetDistributionSb= 0.0f;
		assetDistributionEfg= 0.0f;
		assetDistributionIegs= 0.0f;	
	}
 */

}
