package se.uu.ebc.bemanning.vo;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import se.uu.ebc.bemanning.entity.Person;
import se.uu.ebc.bemanning.entity.Staff;
import se.uu.ebc.bemanning.enums.UserRoleType;
import se.uu.ebc.bemanning.enums.EmploymentType;

import org.apache.log4j.Logger;

public class StaffVO {

    
    private static Logger logger = Logger.getLogger(StaffVO.class.getName());

    private Long id;

	private Long personId;
	private Long organisationUnitId;

    private String program;
    private Float percentGU;
    private Float hoursGU;
    
    private EmploymentType position;

    private Float hourlyCharge;
    private Float maxHourlyCharge;

    private String year;
    private String note;
    private Float ib;
    private Float ubLastYear;
    private Float accumulatedHours;

	private String name;
	private String formName;
	private String ouDesignation;


	
 	/* Setters and getters */



    public Long getId()
    {
    	return this.id;
    }

    public void setId(Long id)
    {
    	this.id = id;
    }



	public Long getPersonId()
	{
		return this.personId;
	}

	public void setPersonId(Long personId)
	{
		this.personId = personId;
	}


	public Long getOrganisationUnitId()
	{
		return this.organisationUnitId;
	}

	public void setOrganisationUnitId(Long organisationUnitId)
	{
		this.organisationUnitId = organisationUnitId;
	}



    public String getProgram()
    {
    	return this.program;
    }

    public void setProgram(String program)
    {
    	this.program = program;
    }


    public Float getPercentGU()
    {
    	return this.percentGU;
    }

    public void setPercentGU(Float percentGU)
    {
    	this.percentGU = percentGU;
    }


    public Float getHoursGU()
    {
    	return this.hoursGU;
    }

    public void setHoursGU(Float hoursGU)
    {
    	this.hoursGU = hoursGU;
    }

    

    public EmploymentType getPosition()
    {
    	return this.position;
    }

    public void setPosition(EmploymentType position)
    {
    	this.position = position;
    }



    public Float getHourlyCharge()
    {
    	return this.hourlyCharge;
    }

    public void setHourlyCharge(Float hourlyCharge)
    {
    	this.hourlyCharge = hourlyCharge;
    }


    public String getYear()
    {
    	return this.year;
    }

    public void setYear(String year)
    {
    	this.year = year;
    }


    public String getNote()
    {
    	return this.note;
    }

    public void setNote(String note)
    {
    	this.note = note;
    }


    public Float getIb()
    {
    	return this.ib;
    }

    public void setIb(Float ib)
    {
    	this.ib = ib;
    }


    public Float getUbLastYear()
    {
    	return this.ubLastYear;
    }

    public void setUbLastYear(Float ubLastYear)
    {
    	this.ubLastYear = ubLastYear;
    }



	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}


	public String getOuDesignation()
	{
		return this.ouDesignation;
	}

	public void setOuDesignation(String ouDesignation)
	{
		this.ouDesignation = ouDesignation;
	}


    public Float getAccumulatedHours()
    {
    	return this.accumulatedHours;
    }

    public void setAccumulatedHours(Float accumulatedHours)
    {
    	this.accumulatedHours = accumulatedHours;
    }


    public Float getMaxHourlyCharge()
    {
    	return this.maxHourlyCharge;
    }

    public void setMaxHourlyCharge(Float maxHourlyCharge)
    {
    	this.maxHourlyCharge = maxHourlyCharge;
    }



	public String getFormName()
	{
		return this.formName;
	}

	public void setFormName(String formName)
	{
		this.formName = formName;
	}

    
    /* Public methods */

  
 	/* Constructors */

	public StaffVO() {}
	
	public StaffVO(Staff xe) {


		this.id = xe.getId();

		this.personId = xe.getPerson().getId();
		this.name = xe.getPerson().getName();
		this.formName = xe.getPerson().getFormName();

		this.organisationUnitId = xe.getOrganisationUnit().getId();
		this.ouDesignation = xe.getOrganisationUnit().getSvName();	
		
		this.program = xe.getProgram();
		this.percentGU = xe.getPercentGU();
		this.position = xe.getPosition();
		this.hourlyCharge = xe.getHourlyCharge();
		this.year = xe.getYear();
		this.note = xe.getNote();
		this.ib = xe.getIb();

//		this.hoursGU = xe.getTotalHours();
		
		this.ubLastYear = xe.getPreviousUb();
		this.accumulatedHours = xe.getAccumulatedHours();
		this.maxHourlyCharge = xe.getMaxHourlyCharge();
	}
}