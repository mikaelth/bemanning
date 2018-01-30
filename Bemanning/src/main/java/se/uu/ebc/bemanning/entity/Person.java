package se.uu.ebc.bemanning.entity;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.ElementCollection;
import javax.persistence.CollectionTable;
import javax.persistence.JoinColumn;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;

import javax.validation.constraints.NotNull;

import se.uu.ebc.bemanning.entity.PhDPosition;
import se.uu.ebc.bemanning.entity.Staff;
import se.uu.ebc.bemanning.enums.UserRoleType;

import org.apache.log4j.Logger;

@Entity
@Table(name = "PERSON")
public class Person  extends Auditable {

    
    private static Logger logger = Logger.getLogger(Person.class.getName());
	    
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
    
/* 
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserRole> userRoles;
 */
    
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

 
	@ElementCollection(targetClass = UserRoleType.class)
	@CollectionTable(name="USER_ROLE", joinColumns=@JoinColumn(name="user_fk"))
	@Column(name="ROLE")
	@NotNull
	@Enumerated(EnumType.STRING)
	private Set<UserRoleType> userRoles = new HashSet<UserRoleType>();
 
    
 
	/* Constructors */
	
	public Person() {
//		userRoles = new HashSet<UserRole>();
		staff = new HashSet<Staff>();
		userRoles.add(UserRoleType.Staff);
	}
	
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
    
/* 
    public Set<UserRole> getUserRoles() {
        return userRoles;
    }
    
    public void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
    }
 */
    
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

    public Set<UserRoleType> getUserRoles() {
        return this.userRoles;
    }
    
    public void setUserRoles(Set<UserRoleType> userRoles) {
        this.userRoles = userRoles;
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
  
/* 
  	public Set<UserRole> getRoles() {
  		return this.roles;
  	}
  	
  	public void setRoles( Set<UserRole> roles) {
  		this.roles = roles;
  	}

  */
}
