package se.uu.ebc.bemanning.entity;

import java.util.Set;
import java.util.Date;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.OneToMany;
import  se.uu.ebc.bemanning.entity.Assignment;
import  se.uu.ebc.bemanning.entity.CourseInstance;
import  se.uu.ebc.bemanning.entity.Staff;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import org.hibernate.annotations.Formula;

import org.apache.log4j.Logger;

import se.uu.ebc.bemanning.enums.EmploymentType;

@Entity
@Table(name = "STAFF")
public class Staff extends Auditable {

    private static Logger logger = Logger.getLogger(Staff.class.getName());

    private final static int YEARLY_HOURS = 1700;
	private final static int LECTURE_HOUR_COST = 1285;
    
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

    
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    @JoinColumn(name = "PERSON_FK")
	private Person person;

	public Person getPerson()
	{
		return this.person;
	}
	public void setPerson(Person person)
	{
		this.person = person;
	}

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    @JoinColumn(name = "OU_FK")
	private OrganisationUnit organisationUnit;


    @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY)
    private Set<Assignment> assignments;
    
    @OneToMany(mappedBy = "courseLeader", fetch = FetchType.LAZY)
    private Set<CourseInstance> courseInstances;
    

    @Column(name = "PROGRAM", length = 255)
    private String program;
    
    @Column(name = "PERCENT_G_U", precision = 12)
    private Float percentGU;
    

	@Enumerated(EnumType.STRING)    
//	@Column(name = "POSITION", length = 255)
    private EmploymentType position;
    
    @Column(name = "HOURLY_CHARGE", precision = 12)
    private Float hourlyCharge;
    
//	@Column(name = "YEAR", length = 255)
    private String year;
    
    @Column(name = "NOTE", length = 255)
    private String note;
    
    @Column(name = "IB", precision = 12)
    private Float ib;
    
/* 
    @Column(name = "UB", precision = 12)
    private Float ub;
    
 */
    
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OLD_ID")
	private Staff previousYearAppointment;

	public Staff getPreviousYearAppointment()
	{
		return this.previousYearAppointment;
	}

	public void setPreviousYearAppointment(Staff previousYearAppointment)
	{
		this.previousYearAppointment = previousYearAppointment;
	}


    @ManyToOne
    @JoinColumns({
        @JoinColumn(
            name = "year",
            referencedColumnName = "STAFFYEAR", insertable = false, updatable = false),
        @JoinColumn(
            name = "position",
            referencedColumnName = "STAFFPOSITION", insertable = false, updatable = false)
    })	
    private MaxCost maxCost;

 
    public MaxCost getMaxCost()
    {
    	return this.maxCost;
    }

    public void setMaxCost(MaxCost maxCost)
    {
    	this.maxCost = maxCost;
    }




	/* Computed properties*/
	
	public Float getMaxHourlyCharge()
	{
		return this.maxCost.getMaxHourlyCharge();
	}

	
	/* Setters and getters */
	
    public Set<Assignment> getAssignments() {
        return assignments;
    }
    
    public void setAssignments(Set<Assignment> assignments) {
        this.assignments = assignments;
    }
    
    public Set<CourseInstance> getCourseInstances() {
        return courseInstances;
    }
    
    public void setCourseInstances(Set<CourseInstance> courseInstances) {
        this.courseInstances = courseInstances;
    }
    
    public String getProgram() {
        return program;
    }
    
    public void setProgram(String program) {
        this.program = program;
    }
    
    public void setPercentGU(Float percentGU) {
        this.percentGU = percentGU;
    }
    
    public EmploymentType getPosition() {
        return position;
    }
    
    public void setPosition(EmploymentType position) {
        this.position = position;
    }
    
    public Float getHourlyCharge() {
        return hourlyCharge;
    }
    
    public void setHourlyCharge(Float hourlyCharge) {
        this.hourlyCharge = hourlyCharge;
    }
    
    public String getYear() {
        return year;
    }
    
    public void setYear(String year) {
        this.year = year;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    public Float getIb() {
        return ib;
    }
    
    public void setIb(Float ib) {
        this.ib = ib;
    }
    
/* 
    public Float getUb() {
        return ub;
    }
    
    public void setUb(Float ub) {
        this.ub = ub;
    }
 */
    
	public OrganisationUnit getOrganisationUnit()
	{
		return this.organisationUnit;
	}

	public void setOrganisationUnit(OrganisationUnit organisationUnit)
	{
		this.organisationUnit = organisationUnit;
	}

   
    
    
    /* Public methods */
    
    
    public java.lang.String getComment()
    {
        return getPerson().getNote()+" "+getNote();
    }

    public float getTotalHours()
    {
        float theSum = 0.0f;
        
        if (assignments != null) {
			for (Assignment theAssignment : assignments) {
				theSum+= theAssignment.getTotalHours();
			logger.debug(theAssignment.getCourseInstance().getCourse().getSeName()+", "+theAssignment.getTotalHours());	
			}
        }
//		logger.debug("totalHours " + theSum + ", and stream " + getTotalHours(new Date()));	
		return theSum;
      
    }

	public float getTotalHours(Date atDate) {
	
        try {
			if (assignments != null) {
				Double hours = assignments.stream()
					.filter(a -> a.getCourseInstance().getEndDate().before(atDate) )
.filter(a -> {logger.debug(a.getCourseInstance().getCourse().getSeName()+", "+a.getTotalHours()); return true;	} )
					.collect(Collectors.summingDouble(a -> a.getTotalHours()));

				return hours.floatValue();
			} else {
				return 0.0f;
			}
		} catch (Exception e) {
			logger.error("Caught a pesky exception " + e +", " + e.getCause());	
			return 0.0f;
		}
	}

    public float getUb()
    {
        // @todo implement public float computeUB()
        
        return getTotalHours() - getPercentGU() * YEARLY_HOURS + getIb();
    }
    
    public float getPreviousUb() {
  		try {
  			return previousYearAppointment == null ? 0.0f : previousYearAppointment.getUb();
  		} catch (Exception e) {
  			logger.error ("getPreviousUb caught a pesky exeption " + e);
  			return 0.0f;
  		}
    	
    }

	public float getAccumulatedHours() {
 		
 		float hours = 0.0f;
 		try {	
		
			hours = this.previousYearAppointment == null ? this.getTotalHours() : this.previousYearAppointment.getAccumulatedHours() + this.getTotalHours();
  		
  		} catch (Exception e) {
  			logger.error ("getAccumulatedHours caught a pesky exeption " + e);
  		} finally {
  			return hours;
  		}
    	
		
	}    
    
    
    public float computeAppointmentCost() 
    {    	
    	float cost = 0.0f;

    	if (this.getPosition() == EmploymentType.Timarvoderad) {
    		cost = LECTURE_HOUR_COST * this.getPlainTeachingHours();
    	} else {
    		cost = this.getPercentGU() * YEARLY_HOURS * this.getHourlyCost();
    	}
    	
    	return cost;
    }

/* 
    public float computeAssignmentCost(String dept) 
    {    	
    	float cost = 0.0f;

    	if (dept!=null || dept.equals(this.getOu().getEconomyHolder(this.getYear()).getAbbreviation())) {
    		cost = this.getHourlyCost() * this.getTotalHours();
    	} else {
    		cost = this.getPlainTeachingHours() < 10 ? LECTURE_HOUR_COST * this.getPlainTeachingHours() : this.getHourlyCost() * this.getTotalHours();
    	}
    	
    	return cost;
    }
 */


	public float getHourlyCost()
	{
		return (this.getHourlyCharge() == 0 || this.getHourlyCharge() == null) ? this.getMaxHourlyCharge() : this.getHourlyCharge() ;	
	}
	

	private float getPlainTeachingHours()
    {
        float theSum = 0;
        for (Assignment theAssignment : getAssignments()) {
        	theSum+= theAssignment.getPlainTeachingHours();
        }
        return theSum;
    }


    public Float getPercentGU()
    {
        Float gu = 0.0f;

		try {
			if ( (this.getPosition()!= null) && this.getPosition().equals(EmploymentType.Doktorand) && this.getPerson().isPhDEnrolled() ) {
				gu = this.getPerson().getPhDPosition().yearlyGU(this.getYear());
			} else {
				gu = percentGU;
			}
	
// 			if (logger.isDebugEnabled()) {
// 				logger.debug("MTh getPercentGU "+ super.getPercentGU()+", "+gu+", "+this.getPosition().equals(EmploymentType.DOKTORAND)+", "+this.getPerson().isPhDEnrolled());
// 			}
       	} catch (Exception ex) {
//			logger.error("MTh getPercentGU experienced pesky exception "+ ex);
			System.out.println("MTh getPercentGU experienced pesky exception "+ ex);
			      	
       	} finally {
        	return gu;
        }
    }

}
