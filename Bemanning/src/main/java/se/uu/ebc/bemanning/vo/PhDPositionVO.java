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

public class PhDPositionVO {

    
    private static Logger logger = Logger.getLogger(PhDPositionVO.class.getName());



    private Long id;

	private Long personId;

    private Date start;
    private Date dissertation;

    private String note;
    private boolean inactive;

	private Date predictedFinishDate;
 	private Date predictedHalfTime;
	private Date predicted80Percent;
	private Float currentRemainingProjectTime;

	private String program;
	

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


    public Date getStart()
    {
    	return this.start;
    }

    public void setStart(Date start)
    {
    	this.start = start;
    }


    public Date getDissertation()
    {
    	return this.dissertation;
    }

    public void setDissertation(Date dissertation)
    {
    	this.dissertation = dissertation;
    }


    public String getNote()
    {
    	return this.note;
    }

    public void setNote(String note)
    {
    	this.note = note;
    }


    public boolean getInactive()
    {
    	return this.inactive;
    }

    public void setInactive(boolean inactive)
    {
    	this.inactive = inactive;
    }


	public Date getPredictedFinishDate()
	{
		return this.predictedFinishDate;
	}

	public void setPredictedFinishDate(Date predictedFinishDate)
	{
		this.predictedFinishDate = predictedFinishDate;
	}


 	public Date getPredictedHalfTime()
 	{
 		return this.predictedHalfTime;
 	}

 	public void setPredictedHalfTime(Date predictedHalfTime)
 	{
 		this.predictedHalfTime = predictedHalfTime;
 	}


	public Date getPredicted80Percent()
	{
		return this.predicted80Percent;
	}

	public void setPredicted80Percent(Date predicted80Percent)
	{
		this.predicted80Percent = predicted80Percent;
	}


	public Float getCurrentRemainingProjectTime()
	{
		return this.currentRemainingProjectTime;
	}

	public void setCurrentRemainingProjectTime(Float currentRemainingProjectTime)
	{
		this.currentRemainingProjectTime = currentRemainingProjectTime;
	}


	public String getProgram()
	{
		return this.program;
	}

	public void setProgram(String program)
	{
		this.program = program;
	}


	/* Constructors */
	
	public PhDPositionVO() {}
	
	public PhDPositionVO(PhDPosition xe, String program) {
		this.id = xe.getId();
		this.personId = xe.getPerson().getId();

		this.start = xe.getStart();
		this.dissertation = xe.getDissertation();
		this.note = xe.getNote();
		this.inactive = xe.getInactive();

		this.predictedFinishDate = xe.predictedFinishDate();
		this.predictedHalfTime = xe.predictedHalfTime();
		this.predicted80Percent = xe.predicted80Percent();

		this.currentRemainingProjectTime = xe.currentRemainingProjectTime();
		

		this.program = program;
	}	
}