package se.uu.ebc.bemanning.vo;


import se.uu.ebc.bemanning.entity.YearsOfHierarchy;
import se.uu.ebc.bemanning.entity.OrganisationUnit;

import org.apache.log4j.Logger;

public class YoHVO {

    private Long id;
    private Integer firstYear;
    private Integer lastYear;

	private Long superUnitId;
	private Long unitId;

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


    public Integer getFirstYear()
    {
    	return this.firstYear;
    }

    public void setFirstYear(Integer firstYear)
    {
    	this.firstYear = firstYear;
    }


    public Integer getLastYear()
    {
    	return this.lastYear;
    }

    public void setLastYear(Integer lastYear)
    {
    	this.lastYear = lastYear;
    }


	public Long getSuperUnitId()
	{
		return this.superUnitId;
	}

	public void setSuperUnitId(Long superUnitId)
	{
		this.superUnitId = superUnitId;
	}

	public Long getUnitId()
	{
		return this.unitId;
	}

	public void setUnitId(Long unitId)
	{
		this.unitId = unitId;
	}

	public String getNote()
	{
		return this.note;
	}

	public void setNote(String note)
	{
		this.note = note;
	}



	/* Constructors */

	public YoHVO(){}

	public YoHVO(YearsOfHierarchy xe){
		this.id = xe.getId();
		this.firstYear = xe.getFirstYear();
		this.lastYear = xe.getLastYear();
		this.superUnitId = xe.getSuperUnit().getId();
		this.unitId = xe.getSubUnit().getId();

		this.note = xe.getNote();	
	}
}