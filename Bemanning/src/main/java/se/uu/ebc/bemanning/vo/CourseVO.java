package  se.uu.ebc.bemanning.vo;

import java.util.Set;

import  se.uu.ebc.bemanning.entity.Course;

public class CourseVO {

    private Long id;
    
    public Long getId() {
        return this.id;
    }    
    public void setId(Long id) {
        this.id = id;
    }

    private String code;
    
    private String seName;
    
    private String enName;
    
    private String courseGroup;
    
    private String period;

    private String note;
    
    private Float credits;
    
/* 
    private Integer effortAdmin;
    
    private Integer effortDevelopment;
    
    private Integer effortLecture;
    
    private Integer effortPractical;
    
    private Integer effortExcursion;
    
    private Integer effortSeminar;
 */
    
    

	/* Constructors */
	
	public CourseVO() {}
	
	public CourseVO(Course c) {
		this.id = c.getId();
		this.code = c.getCode();
		this.seName = c.getSeName();
		this.enName = c.getEnName();
		this.courseGroup = c.getCourseGroup();
		this.period = c.getPeriod();
		this.note = c.getNote();
		this.credits = c.getCredits();
	}
	
	/* Setters and getters */ 
	    
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
    
/* 
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
 */
    
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
    
}
