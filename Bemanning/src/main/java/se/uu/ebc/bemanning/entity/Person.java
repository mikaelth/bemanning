package se.uu.ebc.bemanning.entity;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import javax.validation.constraints.NotNull;

import se.uu.ebc.bemanning.entity.PhDPosition;
import se.uu.ebc.bemanning.entity.Staff;
import se.uu.ebc.bemanning.security.UserRole;

@Entity
@Table(name = "PERSON")
public class Person {
    
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

    
    @OneToOne(mappedBy = "person")
    private PhDPosition phDPosition;
    
    @OneToMany(mappedBy = "person")
    private Set<Staff> staff;
    
    @OneToMany(mappedBy = "user")
    private Set<UserRole> userRoles;
    
    @Column(name = "GIVEN_NAME", length = 255)
    @NotNull
    private String givenName;
    
    @Column(name = "FAMILY_NAME", length = 255)
    @NotNull
    private String familyName;
    
    @Column(name = "FAMILY_FIRST")
    @NotNull
    private Boolean familyFirst;
    
    @Column(name = "NOTE", length = 255)
    private String note;
    
    @Column(name = "USERNAME", length = 255, unique = true)
    @NotNull
    private String username;
    
    @Column(name = "IS_ACTIVE")
    private Boolean isActive;
    
 
 	/* Setters and getters */
 	   
    public PhDPosition getPhDPosition() {
        return phDPosition;
    }
    
    public void setPhDPosition(PhDPosition phDPosition) {
        this.phDPosition = phDPosition;
    }
    
    public Set<Staff> getStaff() {
        return staff;
    }
    
    public void setStaff(Set<Staff> staff) {
        this.staff = staff;
    }
    
    public Set<UserRole> getUserRoles() {
        return userRoles;
    }
    
    public void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
    }
    
    public String getGivenName() {
        return givenName;
    }
    
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }
    
    public String getFamilyName() {
        return familyName;
    }
    
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }
    
    public Boolean getFamilyFirst() {
        return familyFirst;
    }
    
    public Boolean isFamilyFirst() {
        return familyFirst;
    }
    public void setFamilyFirst(Boolean familyFirst) {
        this.familyFirst = familyFirst;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    /* Public methods */

    public java.lang.String getName()
    {
        return 
        	isFamilyFirst() ?
        	getFamilyName()+" "+getGivenName() :
        	getGivenName()+" "+getFamilyName();
    }

    public java.lang.String getFormName()
    {
        return 
        	getFamilyName()+", "+getGivenName();
    }

	public Boolean isPhDEnrolled()
	{
		return ( (this.getPhDPosition() != null) && (this.getPhDPosition().getProgresses().size()>0) );	
	}
    
}
