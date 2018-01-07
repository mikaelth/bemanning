package  se.uu.ebc.bemanning.vo;

import java.util.Set;
import java.util.Date;

import  se.uu.ebc.bemanning.entity.Course;
import  se.uu.ebc.bemanning.entity.CourseInstance;

public class CourseInstanceVO {

    private Long id;
    private String year;
    private String extraDesignation;
    private Date startDate;
    private Date endDate;
    private String note;
    private Integer numberOfStudents;
	private Long courseId;
	private Long courseLeaderId;

	private String courseName;
	private String courseLeaderName;
	private String courseGroup;


	/* Setters and getters */
	

    public Long getId()
    {
    	return this.id;
    }

    public void setId(Long id)
    {
    	this.id = id;
    }


    public String getYear()
    {
    	return this.year;
    }

    public void setYear(String year)
    {
    	this.year = year;
    }


    public String getExtraDesignation()
    {
    	return this.extraDesignation;
    }

    public void setExtraDesignation(String extraDesignation)
    {
    	this.extraDesignation = extraDesignation;
    }


    public Date getStartDate()
    {
    	return this.startDate;
    }

    public void setStartDate(Date startDate)
    {
    	this.startDate = startDate;
    }


    public Date getEndDate()
    {
    	return this.endDate;
    }

    public void setEndDate(Date endDate)
    {
    	this.endDate = endDate;
    }


    public String getNote()
    {
    	return this.note;
    }

    public void setNote(String note)
    {
    	this.note = note;
    }


    public Integer getNumberOfStudents()
    {
    	return this.numberOfStudents;
    }

    public void setNumberOfStudents(Integer numberOfStudents)
    {
    	this.numberOfStudents = numberOfStudents;
    }


	public Long getCourseId()
	{
		return this.courseId;
	}

	public void setCourseId(Long courseId)
	{
		this.courseId = courseId;
	}


	public Long getCourseLeaderId()
	{
		return this.courseLeaderId;
	}

	public void setCourseLeaderId(Long courseLeaderId)
	{
		this.courseLeaderId = courseLeaderId;
	}



	public String getCourseName()
	{
		return this.courseName;
	}

	public void setCourseName(String courseName)
	{
		this.courseName = courseName;
	}


	public String getCourseLeaderName()
	{
		return this.courseLeaderName;
	}

	public void setCourseLeaderName(String courseLeaderName)
	{
		this.courseLeaderName = courseLeaderName;
	}

	public String getCourseGroup()
	{
		return this.courseGroup;
	}

	public void setCourseGroup(String courseGroup)
	{
		this.courseGroup = courseGroup;
	}



	
	/* Constructors */
	
	public CourseInstanceVO(){}
	
	public CourseInstanceVO(CourseInstance xe){

		this.id = xe.getId();

		this.year = xe.getYear();
		this.extraDesignation = xe.getExtraDesignation();
		this.startDate = xe.getStartDate();
		this.endDate = xe.getEndDate();
		this.note = xe.getNote();
		this.numberOfStudents = xe.getNumberOfStudents();

		this.courseId = xe.getCourse().getId();
		this.courseLeaderId = xe.getCourseLeader().getId();

		this.courseName = xe.getCourse().getSeName();
		this.courseGroup = xe.getCourse().getCourseGroup();
		this.courseLeaderName = xe.getCourseLeader().getPerson().getName();	

	}
	
}
