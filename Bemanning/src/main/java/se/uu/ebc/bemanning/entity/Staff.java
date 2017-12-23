package se.uu.ebc.bemanning.entity;

import java.util.Set;
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
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import org.hibernate.annotations.Formula;

import se.uu.ebc.bemanning.enums.EmploymentType;

@Entity
@Table(name = "STAFF")
public class Staff {

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

    
    @ManyToOne
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

    @ManyToOne
    @NotNull
    @JoinColumn(name = "OU_FK")
	private OrganisationUnit organisationUnit;

    @OneToMany(mappedBy = "staff")
    private Set<Assignment> assignments;

    
    @OneToMany(mappedBy = "courseLeader")
    private Set<CourseInstance> courseInstances;
    
    @Column(name = "PROGRAM", length = 255)
    private String program;
    
    @Column(name = "PERCENT_G_U", precision = 12)
    private Float percentGU;
    
  	@Enumerated(EnumType.STRING)    
    @Column(name = "POSITION", length = 255)
    private EmploymentType position;
    
    @Column(name = "HOURLY_CHARGE", precision = 12)
    private Float hourlyCharge;
    
    @Column(name = "YEAR", length = 255)
    private String year;
    
    @Column(name = "NOTE", length = 255)
    private String note;
    
    @Column(name = "IB", precision = 12)
    private Float ib;
    
/* 
    @Column(name = "UB", precision = 12)
    private Float ub;
    
 */
    

	/* Computed properties*/
	
	@Formula("(select max(s.HOURLY_CHARGE) from staff as s where s.YEAR=(select c.YEAR from staff as c where c.id = id) AND s.POSITION=(select c.POSITION from staff as c where c.id = id) )")
	private Float maxHourlyCharge;

	public Float getMaxHourlyCharge()
	{
		return this.maxHourlyCharge;
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
        float theSum = 0;
        for (Assignment theAssignment : assignments) {
        	theSum+= theAssignment.getTotalHours(getPosition());
        }
        return theSum;
    }

    public float getUb()
    {
        // @todo implement public float computeUB()
        
        return getTotalHours() - getPercentGU() * YEARLY_HOURS + getIb();
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
	
// 	private HashMap<String, String> ldapSessionMap = null;
// 
// 	private void doLookup() {
// 
// 		ldapSessionMap = new HashMap<String, String>();
// 		
// 		Properties env = new Properties();
// 		env.put(Context.INITIAL_CONTEXT_FACTORY,
// 				"com.sun.jndi.ldap.LdapCtxFactory");
// 		env.put(Context.PROVIDER_URL,
// 			"ldap://ldap.katalog.uu.se:389");
// 		
// 		try {
// 
// 			if (this.getPerson().getUsername() == null || this.getPerson().getUsername().trim().length() == 0)
// 			{
// 				throw new IllegalArgumentException(
// 					"se.uu.ebc.courseres.service.StaffingService.getPersonAssignments(String year, String username) - 'username' can not be null or empty");
// 			}
// 
// 			InitialLdapContext ctx = new InitialLdapContext(env, null);
// 
// 			Attributes matchAttrs = new BasicAttributes(true);
// 			matchAttrs.put("uid",this.getPerson().getUsername());
// 
// 			if (logger.isDebugEnabled()) {
// 				logger.debug("MTh doLookup finding "+ matchAttrs.get("sn")+", "+matchAttrs.get("givenname"));
// 			}
// 
// 			NamingEnumeration ans = ctx.search("cn=People,dc=uu,dc=se", matchAttrs);
// 
// 			if (logger.isDebugEnabled()) {
// 				logger.debug("MTh doLookup namingEnum "+ ans);
// 			}
// 
// 			while (ans.hasMore()) {
// 				SearchResult sr = (SearchResult) ans.next();
// 				if (logger.isDebugEnabled()) {
// 					logger.debug("MTh doLookup sr "+ sr);
// 				}
// 				Attributes attrs = sr.getAttributes();
// 				if (logger.isDebugEnabled()) {
// 					logger.debug("MTh doLookup attributes "+ attrs);
// 				}
// 				NamingEnumeration ids = attrs.getIDs();
// 				while (ids.hasMore()) {
// 					String id = (String) ids.next();
// 					if (logger.isDebugEnabled()) {
// 						logger.debug("MTh doLookup id "+ id);
// 					}
// 					Attribute val = attrs.get(id);
// 					for (int idx = 0; idx < val.size(); idx++) {
// 						String dir_val = (String) val.get(idx);
// 						ldapSessionMap.put(id, dir_val);
// 						if (logger.isDebugEnabled()) {
// 							logger.debug("MTh doLookup dir_val "+ dir_val);
// 						}
// 					}
// 				}
// 			}
// 
// 
// 		} catch (Exception ne) {
// 			ne.printStackTrace();
// 			logger.error("MTh doLookup experienced pesky exception "+ ne);
// 		}
// 
// 	}
// 	
// 	public String getEmail ()
// 	{
// 		if (logger.isDebugEnabled()) {
// 			logger.debug("MTh getEmail");
// 		}
// 		if (ldapSessionMap == null) {
// 			doLookup();
// 		}
// 		if (logger.isDebugEnabled()) {
// 			logger.debug("MTh getEmail, done LDAP lookup, map " + ldapSessionMap);
// 		}
// 	
// 		return ldapSessionMap.containsKey("mail") ? ldapSessionMap.get("mail") :"";
// 	}
// 	
// 	public String getPhone ()
// 	{
// 		if (logger.isDebugEnabled()) {
// 			logger.debug("MTh getPhone");
// 		}
// 		if (ldapSessionMap == null) {
// 			doLookup();
// 		}
// 		if (logger.isDebugEnabled()) {
// 			logger.debug("MTh getPhone, done LDAP lookup, map " + ldapSessionMap);
// 		}
// 	
// 		return ldapSessionMap.containsKey("telephoneNumber") ? ldapSessionMap.get("telephoneNumber") :"";
// 	}
// 
// 	public String getUuUser ()
// 	{
// 		if (ldapSessionMap == null) {
// 			doLookup();
// 		}
// 
// 		String uuUser = "";
// 		
// 		if (ldapSessionMap.containsKey("edupersonprincipalname")) {
// 			Matcher matcher = Pattern.compile("([\\w\\d]+)@(.*)").matcher(ldapSessionMap.get("edupersonprincipalname"));
// 		
// 			while (matcher.find()) {
// 				uuUser=matcher.group(1);
// 			}
// 		}
// 
// 		return uuUser;
// 	}

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
