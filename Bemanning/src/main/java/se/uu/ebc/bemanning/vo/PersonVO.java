package se.uu.ebc.bemanning.vo;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import se.uu.ebc.bemanning.entity.Person;
import se.uu.ebc.bemanning.enums.UserRoleType;

import org.apache.log4j.Logger;

public class PersonVO {

    
    private static Logger logger = Logger.getLogger(Person.class.getName());
	    
    private Long id;
    
    public Long getId() {
        return this.id;
    }    
    public void setId(Long id) {
        this.id = id;
    }


    private Set<UserRoleType> userRoles;
    
    private String givenName;
    private String familyName;
    private Boolean familyFirst;
    private String note;    
    private String username;
    private Boolean isActive;

	private String formName;
	private String name;

	
 	/* Setters and getters */
 	   
    
    public Set<UserRoleType> getUserRoles() {
        return userRoles;
    }
    
    public void setUserRoles(Set<UserRoleType> userRoles) {
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

	public String getFormName()
	{
		return this.formName;
	}

	public void setFormName(String formName)
	{
		this.formName = formName;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

    
    /* Public methods */

  
 	/* Constructors */

	public PersonVO (Person p) {

		this.id= p.getId();
		this.givenName= p.getGivenName();
		this.familyName= p.getFamilyName();
		this.note= p.getNote();
		this.username= p.getUsername();
		this.isActive= p.getIsActive(); 
		this.familyFirst= p.getFamilyFirst();
		
		this.userRoles= p.getUserRoles();


		this.formName = p.getFormName();
		this.name = p.getName();
	}
	
	public PersonVO() {}

}
