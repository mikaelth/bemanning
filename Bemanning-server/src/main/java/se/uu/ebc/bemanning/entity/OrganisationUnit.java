package se.uu.ebc.bemanning.entity;

import java.util.Set;
import java.util.HashSet;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.FetchType;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import se.uu.ebc.bemanning.entity.assignment.CourseStaffing;
import se.uu.ebc.bemanning.entity.staff.Staff;

@Entity
@Table(name = "ORGANISATION_UNIT")
@Getter
@Setter
//@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode(callSuper = true)
@Slf4j
public class OrganisationUnit  extends Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

 
	@OneToMany(mappedBy = "assigningDept", fetch = FetchType.LAZY)
    private Set<CourseStaffing> assignments;
    
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
    

    public Boolean isCourseEconomyHolder() {
        return courseEconomyHolder;
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
