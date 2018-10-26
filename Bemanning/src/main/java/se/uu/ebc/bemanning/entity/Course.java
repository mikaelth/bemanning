package  se.uu.ebc.bemanning.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import  se.uu.ebc.bemanning.entity.Course;
import  se.uu.ebc.bemanning.entity.CourseInstance;

@Entity
@Table(name = "COURSE")
public class Course  extends Auditable {

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

    
    @OneToMany(mappedBy = "course")
    private Set<CourseInstance> courseInstances;
    
    @Column(name = "CODE", length = 255)
    private String code;
    
    @Column(name = "SE_NAME", length = 255)
    @NotNull
    private String seName;
    
    @Column(name = "EN_NAME", length = 255)
    private String enName;
    
    @Column(name = "COURSE_GROUP", length = 255)
    private String courseGroup;
    
    @Column(name = "PERIOD", length = 255)
    private String period;
    
    @Column(name = "NOTE", length = 255)
    private String note;
    
    @Column(name = "CREDITS", precision = 12)
    private Float credits;
    
 
 
    @Column(name = "EFFORT_ADMIN")
    private Integer effortAdmin = 0;
    
    @Column(name = "EFFORT_DEVELOPMENT")
    private Integer effortDevelopment = 0;
    
    @Column(name = "EFFORT_LECTURE")
    private Integer effortLecture = 0;
    
    @Column(name = "EFFORT_PRACTICAL")
    private Integer effortPractical = 0;
    
    @Column(name = "EFFORT_EXCURSION")
    private Integer effortExcursion = 0;
    
    @Column(name = "EFFORT_SEMINAR")
    private Integer effortSeminar = 0;
    
	@ManyToOne
    @JoinColumn(name = "PRIM_EXAM_FK")
	private Person primaryExaminer;

	@ManyToOne
    @JoinColumn(name = "SEC_EXAM_FK")
	private Person secondaryExaminer;


	/* Setters and getters */
 
    public Set<CourseInstance> getCourseInstances() {
        return courseInstances;
    }
    
    public void setCourseInstances(Set<CourseInstance> courseInstances) {
        this.courseInstances = courseInstances;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getSeName() {
        return seName;
    }
    
    public void setSeName(String seName) {
        this.seName = seName;
    }
    
    public String getEnName() {
        return enName;
    }
    
    public void setEnName(String enName) {
        this.enName = enName;
    }
    
    public String getCourseGroup() {
        return courseGroup;
    }
    
    public void setCourseGroup(String courseGroup) {
        this.courseGroup = courseGroup;
    }
    
    public String getPeriod() {
        return period;
    }
    
    public void setPeriod(String period) {
        this.period = period;
    }
    
    public Integer getEffortAdmin() {
        return effortAdmin;
    }
    
    public void setEffortAdmin(Integer effortAdmin) {
        this.effortAdmin = effortAdmin;
    }
    
    public Integer getEffortDevelopment() {
        return effortDevelopment;
    }
    
    public void setEffortDevelopment(Integer effortDevelopment) {
        this.effortDevelopment = effortDevelopment;
    }
    
    public Integer getEffortLecture() {
        return effortLecture;
    }
    
    public void setEffortLecture(Integer effortLecture) {
        this.effortLecture = effortLecture;
    }
    
    public Integer getEffortPractical() {
        return effortPractical;
    }
    
    public void setEffortPractical(Integer effortPractical) {
        this.effortPractical = effortPractical;
    }
    
    public Integer getEffortExcursion() {
        return effortExcursion;
    }
    
    public void setEffortExcursion(Integer effortExcursion) {
        this.effortExcursion = effortExcursion;
    }
    
    public Integer getEffortSeminar() {
        return effortSeminar;
    }
    
    public void setEffortSeminar(Integer effortSeminar) {
        this.effortSeminar = effortSeminar;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    public Float getCredits() {
        return credits;
    }
    
    public void setCredits(Float credits) {
        this.credits = credits;
    }

	public Person getPrimaryExaminer()
	{
		return this.primaryExaminer;
	}

	public void setPrimaryExaminer(Person primaryExaminer)
	{
		this.primaryExaminer = primaryExaminer;
	}

	public Person getSecondaryExaminer()
	{
		return this.secondaryExaminer;
	}

	public void setSecondaryExaminer(Person secondaryExaminer)
	{
		this.secondaryExaminer = secondaryExaminer;
	}
    
}
