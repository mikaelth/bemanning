package se.uu.ebc.bemanning.entity;

import java.util.Set;
import java.util.HashSet;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.persistence.FetchType;


@Entity
@Table(name = "ORGANISATION_UNIT")
public class OrganisationUnit {
    
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

 
	@OneToMany(mappedBy = "assigningDept", fetch = FetchType.LAZY)
    private Set<Assignment> assignments;
    
    @OneToMany(mappedBy = "debitUnit", fetch = FetchType.LAZY)
    private Set<CourseGrant> debitGrants;
    
    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private Set<CourseGrant> creditGrants;
    
    @OneToMany(mappedBy = "organisationUnit", fetch = FetchType.LAZY)
    private Set<Staff> staff;
    
    @OneToMany(mappedBy = "superUnit", fetch = FetchType.LAZY)
    private Set<YearsOfHierarchy> subUnits;
    
    @OneToMany(mappedBy = "subUnit", fetch = FetchType.LAZY)
    private Set<YearsOfHierarchy> superUnits;

    
    @Column(name = "SV_NAME", length = 255)
    @NotNull
    private String svName;
    
    @Column(name = "EN_NAME", length = 255)
    @NotNull
    private String enName;
    
    @Column(name = "UNIT_KIND", length = 255)
    @NotNull
    private String unitKind;
    
    @Column(name = "ABBREVIATION", length = 255, unique = true)
    private String abbreviation;
    
    @Column(name = "IN_SYSTEM")
    private Boolean inSystem;
    
    @Column(name = "LEGACY_UNIT")
    private Boolean legacyUnit;
    
    @Column(name = "COURSE_ECONOMY_HOLDER")
    private Boolean courseEconomyHolder;
    

	/* Setters and getters */
	
    public Set<Assignment> getAssignments() {
        return assignments;
    }
    
    public void setAssignments(Set<Assignment> assignments) {
        this.assignments = assignments;
    }
    
    public Set<CourseGrant> getCreditGrants() {
        return creditGrants;
    }
    
    public void setCreditGrants(Set<CourseGrant> creditGrants) {
        this.creditGrants = creditGrants;
    }
    
    public Set<CourseGrant> getDebitGrants() {
        return debitGrants;
    }
    
    public void setDebitGrants(Set<CourseGrant> debitGrants) {
        this.debitGrants = debitGrants;
    }
    
    public Set<Staff> getStaff() {
        return staff;
    }
    
    public void setStaff(Set<Staff> staff) {
        this.staff = staff;
    }
    
    public Set<YearsOfHierarchy> getSubUnits() {
        return this.subUnits;
    }
    
    public void setSubUnits(Set<YearsOfHierarchy> subUnits) {
        this.subUnits = subUnits;
    }
    
    public Set<YearsOfHierarchy> getSuperUnits() {
        return this.superUnits;
    }
    
    public void setSuperUnits(Set<YearsOfHierarchy> superUnits) {
        this.superUnits = superUnits;
    }
    
    public String getSvName() {
        return svName;
    }
    
    public void setSvName(String svName) {
        this.svName = svName;
    }
    
    public String getEnName() {
        return enName;
    }
    
    public void setEnName(String enName) {
        this.enName = enName;
    }
    
    public String getUnitKind() {
        return unitKind;
    }
    
    public void setUnitKind(String unitKind) {
        this.unitKind = unitKind;
    }
    
    public String getAbbreviation() {
        return abbreviation;
    }
    
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }
    
    public Boolean getInSystem() {
        return inSystem;
    }
    
    public void setInSystem(Boolean inSystem) {
        this.inSystem = inSystem;
    }
    
    public Boolean getLegacyUnit() {
        return legacyUnit;
    }
    
    public void setLegacyUnit(Boolean legacyUnit) {
        this.legacyUnit = legacyUnit;
    }
    
    public Boolean getCourseEconomyHolder() {
        return courseEconomyHolder;
    }

    public Boolean isCourseEconomyHolder() {
        return courseEconomyHolder;
    }
    
    public void setCourseEconomyHolder(Boolean courseEconomyHolder) {
        this.courseEconomyHolder = courseEconomyHolder;
    }
  
  
  
	/* Public methods */
	
    public OrganisationUnit getEconomyHolder(String year)
    {

		if (this.isCourseEconomyHolder() || this == this.getSuperUnit(year)) {
			return this;
		} else {
			return this.getSuperUnit(year).getEconomyHolder(year);
		}			
    }

    public OrganisationUnit getCurrentSuperUnit()
    {
		return getSuperUnit(Integer.toString( Calendar.getInstance().get(Calendar.YEAR) ));
    }

    public OrganisationUnit getSuperUnit(String year)
    {
		OrganisationUnit csu = this;
		for (YearsOfHierarchy theUnit : this.getSuperUnits()) {
			if (theUnit.valid(year)) {
				csu = theUnit.getSuperUnit();
			}
		}
		
		return csu;
    }

    public List<OrganisationUnit> getSubUnits(String year)
    {

		List<OrganisationUnit> csus = new ArrayList<OrganisationUnit>();

		for (YearsOfHierarchy theUnit : this.getSubUnits()) {
			if (theUnit.valid(year)) {
				csus.add(theUnit.getSubUnit());
			}
		}
		
		return csus;
    }
    
    public List<OrganisationUnit> getExpandedOu(String year)
    {
		
		List<OrganisationUnit> csus = new ArrayList<OrganisationUnit>();
		csus.add(this);
		
		for (OrganisationUnit theSubUnit : this.getSubUnits(year)) {
			csus.addAll(theSubUnit.getExpandedOu(year));
		}
		
		return csus;
    }
 
    public String[] getExpandedOuAbbreviations(String year)
    {
		
		List<OrganisationUnit> ous = this.getExpandedOu(year);
		Set<String> abs = new HashSet<String>();
		for (OrganisationUnit ou : ous) {
			abs.add(ou.getAbbreviation());
		}
		
		return abs.toArray(new String[0]);
    }
	    
}
