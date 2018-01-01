package se.uu.ebc.bemanning.vo;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import se.uu.ebc.bemanning.entity.Assignment;

import org.apache.log4j.Logger;

public class AssignmentVO {

    
    private static Logger logger = Logger.getLogger(AssignmentVO.class.getName());
	    
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

	
 	/* Setters and getters */
 	   

	public Long getId()
	{
		return this.id;
	}

   public void setId(Long id)
   {
		this.id = id;
   }


	public Long getStaffId()
	{
		return this.staffId;
	}

	public void setStaffId(Long staffId)
	{
		this.staffId = staffId;
	}


	public Long getCourseInstanceId()
	{
		return this.courseInstanceId;
	}

	public void setCourseInstanceId(Long courseInstanceId)
	{
		this.courseInstanceId = courseInstanceId;
	}


	public String getAssigningDept()
	{
		return this.assigningDept;
	}

	public void setAssigningDept(String assigningDept)
	{
		this.assigningDept = assigningDept;
	}



    public Float getHoursAdmin()
    {
    	return this.hoursAdmin;
    }

    public void setHoursAdmin(Float hoursAdmin)
    {
    	this.hoursAdmin = hoursAdmin;
    }


    public Float getHoursDevelopment()
    {
    	return this.hoursDevelopment;
    }

    public void setHoursDevelopment(Float hoursDevelopment)
    {
    	this.hoursDevelopment = hoursDevelopment;
    }


    public Float getHoursLecture()
    {
    	return this.hoursLecture;
    }

    public void setHoursLecture(Float hoursLecture)
    {
    	this.hoursLecture = hoursLecture;
    }


    public Float getHoursPractical()
    {
    	return this.hoursPractical;
    }

    public void setHoursPractical(Float hoursPractical)
    {
    	this.hoursPractical = hoursPractical;
    }


    public Float getHoursExcursion()
    {
    	return this.hoursExcursion;
    }

    public void setHoursExcursion(Float hoursExcursion)
    {
    	this.hoursExcursion = hoursExcursion;
    }


    public Float getHoursSeminar()
    {
    	return this.hoursSeminar;
    }

    public void setHoursSeminar(Float hoursSeminar)
    {
    	this.hoursSeminar = hoursSeminar;
    }



    public String getNote()
    {
    	return this.note;
    }

    public void setNote(String note)
    {
    	this.note = note;
    }

    
    /* Public methods */

  
 	/* Constructors */

	public AssignmentVO (Assignment xe) {

		this.id = xe.getId();
		this.staffId = xe.getStaff().getId();
		this.courseInstanceId = xe.getCourseInstance().getId();
		this.assigningDept = xe.getAssigningDept().getAbbreviation();

		this.hoursAdmin = xe.getHoursAdmin();
		this.hoursDevelopment = xe.getHoursDevelopment();
		this.hoursLecture = xe.getHoursLecture();
		this.hoursPractical = xe.getHoursPractical();
		this.hoursExcursion = xe.getHoursExcursion();
		this.hoursSeminar = xe.getHoursSeminar();
		this.note = xe.getNote();
	}
	
	public AssignmentVO() {}

}
