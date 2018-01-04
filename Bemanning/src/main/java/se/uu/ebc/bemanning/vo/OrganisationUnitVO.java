package se.uu.ebc.bemanning.vo;


import se.uu.ebc.bemanning.entity.OrganisationUnit;

import org.apache.log4j.Logger;

public class OrganisationUnitVO {


    private Long id;
    private String svName;
    private String enName;
    private String unitKind;
    private String abbreviation;
    private Boolean inSystem;
    private Boolean legacyUnit;
    private Boolean courseEconomyHolder;

	/* Gettes and setters */


    public Long getId()
    {
    	return this.id;
    }

    public void setId(Long id)
    {
    	this.id = id;
    }



    public String getSvName()
    {
    	return this.svName;
    }

    public void setSvName(String svName)
    {
    	this.svName = svName;
    }


    public String getEnName()
    {
    	return this.enName;
    }

    public void setEnName(String enName)
    {
    	this.enName = enName;
    }


    public String getUnitKind()
    {
    	return this.unitKind;
    }

    public void setUnitKind(String unitKind)
    {
    	this.unitKind = unitKind;
    }


    public String getAbbreviation()
    {
    	return this.abbreviation;
    }

    public void setAbbreviation(String abbreviation)
    {
    	this.abbreviation = abbreviation;
    }



    public Boolean getInSystem()
    {
    	return this.inSystem;
    }

    public void setInSystem(Boolean inSystem)
    {
    	this.inSystem = inSystem;
    }


    public Boolean getLegacyUnit()
    {
    	return this.legacyUnit;
    }

    public void setLegacyUnit(Boolean legacyUnit)
    {
    	this.legacyUnit = legacyUnit;
    }


    public Boolean getCourseEconomyHolder()
    {
    	return this.courseEconomyHolder;
    }

    public void setCourseEconomyHolder(Boolean courseEconomyHolder)
    {
    	this.courseEconomyHolder = courseEconomyHolder;
    }


	/* Constructors */
	
	public OrganisationUnitVO() {}
	
	public OrganisationUnitVO(OrganisationUnit ou) {
	

		this.id = ou.getId();
		this.svName = ou.getSvName();
		this.enName = ou.getEnName();
		this.unitKind = ou.getUnitKind();
		this.abbreviation = ou.getAbbreviation();
		this.inSystem = ou.getInSystem();
		this.legacyUnit = ou.getLegacyUnit();
		this.courseEconomyHolder = ou.getCourseEconomyHolder();
	
	}

}