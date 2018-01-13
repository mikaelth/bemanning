package se.uu.ebc.bemanning.vo;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Date;

import se.uu.ebc.bemanning.entity.PhDPosition;
import se.uu.ebc.bemanning.entity.Person;
import se.uu.ebc.bemanning.entity.Progress;

import org.apache.log4j.Logger;

public class ProgressVO {

    
    private static Logger logger = Logger.getLogger(ProgressVO.class.getName());

    private Long id;

	private Long phdPositionId;
    private Date date;

    private Float activity;
    private Float projectFraction;
    private Float guFraction;
    private boolean toEcoSys;
    private Float remainingMonths;
    private boolean toUpDok;
    private String note;
    private Float addedMonths;


	/* Setters and getters */

    public Long getId()
    {
    	return this.id;
    }

    public void setId(Long id)
    {
    	this.id = id;
    }


	public Long getPhdPositionId()
	{
		return this.phdPositionId;
	}

	public void setPhdPositionId(Long phdPositionId)
	{
		this.phdPositionId = phdPositionId;
	}


    public Date getDate()
    {
    	return this.date;
    }

    public void setDate(Date date)
    {
    	this.date = date;
    }


    public Float getActivity()
    {
    	return this.activity;
    }

    public void setActivity(Float activity)
    {
    	this.activity = activity;
    }


    public Float getProjectFraction()
    {
    	return this.projectFraction;
    }

    public void setProjectFraction(Float projectFraction)
    {
    	this.projectFraction = projectFraction;
    }


    public Float getGuFraction()
    {
    	return this.guFraction;
    }

    public void setGuFraction(Float guFraction)
    {
    	this.guFraction = guFraction;
    }


    public boolean getToEcoSys()
    {
    	return this.toEcoSys;
    }

    public void setToEcoSys(boolean toEcoSys)
    {
    	this.toEcoSys = toEcoSys;
    }


    public Float getRemainingMonths()
    {
    	return this.remainingMonths;
    }

    public void setRemainingMonths(Float remainingMonths)
    {
    	this.remainingMonths = remainingMonths;
    }


    public boolean getToUpDok()
    {
    	return this.toUpDok;
    }

    public void setToUpDok(boolean toUpDok)
    {
    	this.toUpDok = toUpDok;
    }


    public String getNote()
    {
    	return this.note;
    }

    public void setNote(String note)
    {
    	this.note = note;
    }


    public Float getAddedMonths()
    {
    	return this.addedMonths;
    }

    public void setAddedMonths(Float addedMonths)
    {
    	this.addedMonths = addedMonths;
    }


	/* Constructors */
	
	public ProgressVO() {}
	
	public ProgressVO (Progress xe) {

		this.id = xe.getId();
		this.date = xe.getDate();
		this.activity = xe.getActivity();
		this.projectFraction = xe.getProjectFraction();
		this.guFraction = xe.getGuFraction();
		this.toEcoSys = xe.getToEcoSys();
		this.remainingMonths = xe.getRemainingMonths();
		this.toUpDok = xe.getToUpDok();
		this.note = xe.getNote();
		this.addedMonths = xe.getAddedMonths();	

		this.phdPositionId = xe.getPhdPosition().getId();

	}
}