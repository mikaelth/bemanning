package se.uu.ebc.bemanning.vo;

import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import se.uu.ebc.bemanning.entity.Person;
import se.uu.ebc.bemanning.entity.Staff;
import se.uu.ebc.bemanning.enums.UserRoleType;

import org.apache.log4j.Logger;

public class UserVO {

    
    private static Logger logger = Logger.getLogger(UserVO.class.getName());
	 
	private Long id;
	   
    private String username;
  	private String formName;
	private String name;

	private Map<String, String> principalDepts = new HashMap<String, String>();
	private Set<UserRoleType> userRoles = new HashSet<UserRoleType>();

 	/* Setters and getters */
 	   

	public Long getId()
	{
		return this.id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
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

	public Map<String, String> getPrincipalDepts()
	{
		return this.principalDepts;
	}

	public void setPrincipalDepts(Map<String, String> principalDepts)
	{
		this.principalDepts = principalDepts;
	}

	public Set<UserRoleType> getUserRoles()
	{
		return this.userRoles;
	}

	public void setUserRoles(Set<UserRoleType> userRoles)
	{
		this.userRoles = userRoles;
	}
    
    /* Public methods */

  
 	/* Constructors */

	public UserVO (Person p) {

		this.id = p.getId();
		
		this.username= p.getUsername();
		this.formName = p.getFormName();
		this.name = p.getName();
		
		this.userRoles = p.getUserRoles();
		
		for (Staff s : p.getStaff()){
			this.principalDepts.put(s.getYear(),s.getOrganisationUnit().getEconomyHolder(s.getYear()).getAbbreviation());
		}
	}
	
	public UserVO() {}

}
