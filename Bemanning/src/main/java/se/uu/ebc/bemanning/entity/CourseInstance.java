package  se.uu.ebc.bemanning.entity;

import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import  se.uu.ebc.bemanning.entity.Assignment;
import  se.uu.ebc.bemanning.entity.CourseGrant;
import  se.uu.ebc.bemanning.entity.CourseInstance;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.Logger;

@Entity
@Table(name = "COURSE_INSTANCE")
public class CourseInstance  extends Auditable {

    @Transient
    private static Logger logger = Logger.getLogger(CourseInstance.class.getName());
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @NotNull
    @JoinColumn(name = "COURSE_FK")
	private Course course;

	public Course getCourse()
	{
		return this.course;
	}

	public void setCourse(Course course)
	{
		this.course = course;
	}


    @ManyToOne
    @NotNull
    @JoinColumn(name = "COURSE_LEADER_FK")
	private Staff courseLeader;

	public Staff getCourseLeader()
	{
		return this.courseLeader;
	}

	public void setCourseLeader(Staff courseLeader)
	{
		this.courseLeader = courseLeader;
	}


    
    @OneToMany(mappedBy = "courseInstance")
    private Set<Assignment> assignments;
    
    @OneToMany(mappedBy = "courseInstance")
    private Set<CourseGrant> courseGrants;
    
    @Column(name = "YEAR", length = 255)
    @NotNull
    private String year;
    
    @Column(name = "EXTRA_DESIGNATION", length = 255)
    private String extraDesignation;
    
    @Column(name = "START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "MM")
    private Date startDate;
    
    @Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "MM")
    private Date endDate;
    
    @Column(name = "INCOME_GRANT")
    private Integer incomeGrant;
    
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
    
    @Column(name = "NOTE", length = 255)
    private String note;
    
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
    
    @Column(name = "OLD_ID")
    private Long oldId;
 
 
	@ManyToOne
    @JoinColumn(name = "PRIM_EXAM_FK")
	private Staff primaryExaminer;

	@ManyToOne
    @JoinColumn(name = "SEC_EXAM_FK")
	private Staff secondaryExaminer;

 
 	/* Setters and getters */   
    public Set<Assignment> getAssignments() {
        return assignments;
    }
    
    public void setAssignments(Set<Assignment> assignments) {
        this.assignments = assignments;
    }
    
    public Set<CourseGrant> getCourseGrants() {
        return courseGrants;
    }
    
    public void setCourseGrants(Set<CourseGrant> courseGrants) {
        this.courseGrants = courseGrants;
    }
    
    public String getYear() {
        return year;
    }
    
    public void setYear(String year) {
        this.year = year;
    }
    
    public String getExtraDesignation() {
        return extraDesignation;
    }
    
    public void setExtraDesignation(String extraDesignation) {
        this.extraDesignation = extraDesignation;
    }
    
    public Date getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public Integer getIncomeGrant() {
        return incomeGrant;
    }
    
    public void setIncomeGrant(Integer incomeGrant) {
        this.incomeGrant = incomeGrant;
    }
    
    public Float getAssetDistributionEb() {
        return assetDistributionEb;
    }
    
    public void setAssetDistributionEb(Float assetDistributionEb) {
        this.assetDistributionEb = assetDistributionEb;
    }
    
    public Float getAssetDistributionFb() {
        return assetDistributionFb;
    }
    
    public void setAssetDistributionFb(Float assetDistributionFb) {
        this.assetDistributionFb = assetDistributionFb;
    }
    
    public Float getAssetDistributionMe() {
        return assetDistributionMe;
    }
    
    public void setAssetDistributionMe(Float assetDistributionMe) {
        this.assetDistributionMe = assetDistributionMe;
    }
    
    public Float getAssetDistributionSb() {
        return assetDistributionSb;
    }
    
    public void setAssetDistributionSb(Float assetDistributionSb) {
        this.assetDistributionSb = assetDistributionSb;
    }
    
    public Float getAssetDistributionEfg() {
        return assetDistributionEfg;
    }
    
    public void setAssetDistributionEfg(Float assetDistributionEfg) {
        this.assetDistributionEfg = assetDistributionEfg;
    }
    
    public Float getAssetDistributionIegs() {
        return assetDistributionIegs;
    }
    
    public void setAssetDistributionIegs(Float assetDistributionIegs) {
        this.assetDistributionIegs = assetDistributionIegs;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    public Integer getIncomeAdjustment() {
        return incomeAdjustment;
    }
    
    public void setIncomeAdjustment(Integer incomeAdjustment) {
        this.incomeAdjustment = incomeAdjustment;
    }
    
    public Float getRequiredEffortAdmin() {
        return requiredEffortAdmin;
    }
    
    public void setRequiredEffortAdmin(Float requiredEffortAdmin) {
        this.requiredEffortAdmin = requiredEffortAdmin;
    }
    
    public Float getRequiredEffortDevelopment() {
        return requiredEffortDevelopment;
    }
    
    public void setRequiredEffortDevelopment(Float requiredEffortDevelopment) {
        this.requiredEffortDevelopment = requiredEffortDevelopment;
    }
    
    public Float getRequiredEffortLecture() {
        return requiredEffortLecture;
    }
    
    public void setRequiredEffortLecture(Float requiredEffortLecture) {
        this.requiredEffortLecture = requiredEffortLecture;
    }
    
    public Float getRequiredEffortPractical() {
        return requiredEffortPractical;
    }
    
    public void setRequiredEffortPractical(Float requiredEffortPractical) {
        this.requiredEffortPractical = requiredEffortPractical;
    }
    
    public Float getRequiredEffortExcursion() {
        return requiredEffortExcursion;
    }
    
    public void setRequiredEffortExcursion(Float requiredEffortExcursion) {
        this.requiredEffortExcursion = requiredEffortExcursion;
    }
    
    public Float getRequiredEffortSeminar() {
        return requiredEffortSeminar;
    }
    
    public void setRequiredEffortSeminar(Float requiredEffortSeminar) {
        this.requiredEffortSeminar = requiredEffortSeminar;
    }
    
    public Float getPercentCourse() {
        return percentCourse;
    }
    
    public void setPercentCourse(Float percentCourse) {
        this.percentCourse = percentCourse;
    }
    
    public Integer getNumberOfStudents() {
        return numberOfStudents;
    }
    
    public void setNumberOfStudents(Integer numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }
    
    public Integer getTotalGrant() {
        return totalGrant;
    }
    
    public void setTotalGrant(Integer totalGrant) {
        this.totalGrant = totalGrant;
    }
    
    public Long getOldId() {
        return oldId;
    }
    
    public void setOldId(Long oldId) {
        this.oldId = oldId;
    }

	public Staff getPrimaryExaminer()
	{
		return this.primaryExaminer;
	}

	public void setPrimaryExaminer(Staff primaryExaminer)
	{
		this.primaryExaminer = primaryExaminer;
	}

	public Staff getSecondaryExaminer()
	{
		return this.secondaryExaminer;
	}

	public void setSecondaryExaminer(Staff secondaryExaminer)
	{
		this.secondaryExaminer = secondaryExaminer;
	}




	/* Public methods */
	 
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
	


	/* Constructors */
	
	public CourseInstance() {
		assetDistributionEb= 0.0f;
		assetDistributionFb= 0.0f;
		assetDistributionMe= 0.0f;
		assetDistributionSb= 0.0f;
		assetDistributionEfg= 0.0f;
		assetDistributionIegs= 0.0f;	
	}

}
