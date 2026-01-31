package se.uu.ebc.bemanning.entity;

import java.util.Set;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import se.uu.ebc.bemanning.enums.EmploymentType;

@Entity
@Table(name = "STAFF")
@Getter
@Setter
//@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode(callSuper = true)
@DiscriminatorColumn(name="STAFF_KIND", discriminatorType = DiscriminatorType.STRING)
@Slf4j
public abstract class Staff extends Auditable {

 //   private static Logger logger = LoggerFactory.getLogger(Staff.class);

    private final static int YEARLY_HOURS = 1700;
	private final static int LECTURE_HOUR_COST = 1285;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    
    
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    @JoinColumn(name = "PERSON_FK")
	private Person person;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    @JoinColumn(name = "OU_FK")
	private OrganisationUnit organisationUnit;


    @OneToMany(mappedBy = "staff"/* ,  fetch = FetchType.LAZY */)
    private Set<CourseStaffing> assignments;
    
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
    
    @OneToMany(mappedBy = "primaryExaminer")
    private Set<CourseInstance> primExamCourses;

    @OneToMany(mappedBy = "secondaryExaminer")
    private Set<CourseInstance> secExamCourses;
    
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OLD_ID")
	private Staff previousYearAppointment;


 
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
 

 	/* Computed properties*/
	
	public Float getMaxHourlyCharge()
	{
//		return 500.0f;
		return this.maxCost.getMaxHourlyCharge();
	}

	
    
    /* Public methods */
    
    
    public java.lang.String getComment()
    {
        return getPerson().getNote()+" "+getNote();
    }

  
 /* 
  public float getTotalHours()
    {
        float theSum = 0.0f;
        
        if (assignments != null) {
		log.debug("totalHours, iterating " );	
			for (Assignment theAssignment : assignments) {
				theSum+= theAssignment.getTotalHours();
			log.debug(theAssignment.getCourseInstance().getCourse().getSeName()+", "+theAssignment.getTotalHours());	
			}
        }
		log.debug("totalHours " + theSum + ", and stream " + getTotalHours(new Date()));	
		return theSum;
      
    }
 
 */

	public float getTotalHours() {
	
        try {
			if (assignments != null) {
				log.debug("in getTotalHours ");
				log.debug("assignments " + this.assignments.size());
				
				return this.assignments.stream()
					.filter(a -> {log.debug(a.getCourseInstance().getCourse().getSeName()+", "+a.getTotalHours()); return true;	} )
					.collect(Collectors.summingDouble(a -> a.getTotalHours())).floatValue();
			} else {
				return 0.0f;
			}
		} catch (Exception e) {
			log.error("getTotalHours caught a pesky exception " + e +", " + e.getCause());	
			return 0.0f;
		}
	}

	public float getTotalHours(LocalDateTime atDate) {
	
        try {
			if (assignments != null) {
				Double hours = assignments.stream()
					.filter(a -> a.getCourseInstance().getEndDate().isBefore(atDate))
					.filter(a -> {log.debug(a.getCourseInstance().getCourse().getSeName() + ", " + a.getTotalHours()); return true;})
					.collect(Collectors.summingDouble(a -> a.getTotalHours()));

				return hours.floatValue();
			} else {
				return 0.0f;
			}
		} catch (Exception e) {
			log.error("Caught a pesky exception " + e + ", " + e.getCause());	
			return 0.0f;
		}
	}

    public float getUb()
    {
        // @todo implement public float computeUB()
        
        return getTotalHours() - getPercentGU() * YEARLY_HOURS + getIb();
    }
    
//    public float getPreviousUb() {
    public float getUbLastYear() {
  		try {
			log.debug("getPreviousUb " + previousYearAppointment.getId());
			log.debug("getPreviousUb " +previousYearAppointment.getUb());
  			return previousYearAppointment.getId() == null ? 0.0f : previousYearAppointment.getUb();
  		} catch (Exception e) {
  			log.error ("getPreviousUb caught a pesky exeption " + e);
  			return 0.0f;
  		}
    	
    }

	public float getAccumulatedHours() {
 		
// 		float hours = 0.0f;
 		try {	
		
//			hours = this.previousYearAppointment == null ? this.getTotalHours() : this.previousYearAppointment.getAccumulatedHours() + this.getTotalHours();
			log.debug("Staff id " + this.id + ", " + this.getTotalHours());
  			return this.previousYearAppointment == null ? this.getTotalHours() : this.previousYearAppointment.getAccumulatedHours() + this.getTotalHours();
  		} catch (Exception e) {
  			log.error ("getAccumulatedHours caught a pesky exeption " + e);
  			return 0.0f;
		}
/* 
  		} finally {
  			return hours;
  		}
 */
    		
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
        for (CourseStaffing theAssignment : getAssignments()) {
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
	
       	} catch (Exception ex) {
			log.error("MTh getPercentGU experienced pesky exception "+ ex);
			System.out.println("MTh getPercentGU experienced pesky exception "+ ex);
			      	
       	} finally {
        	return gu;
        }
    }

}
