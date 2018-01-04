package se.uu.ebc.bemanning.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;


@Entity
@Table(name = "YEARS_OF_HIERARCHY")
public class YearsOfHierarchy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    
    @Column(name = "FIRST_YEAR")
    private Integer firstYear;
    
    @Column(name = "LAST_YEAR")
    private Integer lastYear;
    
    @ManyToOne
    @NotNull
    @JoinColumn(name = "SUPER_UNIT_FK")
	private OrganisationUnit superUnit;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "SUB_UNIT_FK")
	private OrganisationUnit subUnit;

    @Column(name = "NOTE")
    private String note;


    @Transient
    private static Logger logger = Logger.getLogger(YearsOfHierarchy.class.getName());

    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    public Integer getFirstYear() {
        return firstYear;
    }
    
    public void setFirstYear(Integer firstYear) {
        this.firstYear = firstYear;
    }
    
    public Integer getLastYear() {
        return lastYear;
    }
    
    public void setLastYear(Integer lastYear) {
        this.lastYear = lastYear;
    }
	public OrganisationUnit getSuperUnit()
	{
		return this.superUnit;
	}

	public void setSuperUnit(OrganisationUnit superUnit)
	{
		this.superUnit = superUnit;
	}


	public OrganisationUnit getSubUnit()
	{
		return this.subUnit;
	}

	public void setSubUnit(OrganisationUnit subUnit)
	{
		this.subUnit = subUnit;
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
    
    public boolean valid(String year)
    {
		boolean valid = false;
		try {
			Integer current = Integer.parseInt(year);
			valid = valid(current);
		} catch (NumberFormatException nx) {
			logger.error("MTh YearsOfHierarchy.valid(String year) experienced pesky exception "+ nx);			
		}
		return valid;
	}

    public boolean valid(Integer year)
    {
		return ( this.getFirstYear()<=year && (this.getLastYear()==null || this.getLastYear()>=year) );
	}
    
    
}
