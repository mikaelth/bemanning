package se.uu.ebc.bemanning.entity;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;

@Entry(base = "ou=user", objectClasses = { "person", "inetOrgPerson", "top" })
public class AKKAUser {

    @Id
    private Name id;
    
    @Attribute(name = "cn") 
    private String username;
	
	@Attribute(name="sn")
	private String familyName;

	private String mail;
 	private String telephoneNumber;



	/* Setters and getters */
	

    public Name getId()
    {
    	return this.id;
    }

    public void setId(Name id)
    {
    	this.id = id;
    }


    public String getUsername()
    {
    	return this.username;
    }

    public void setUsername(String username)
    {
    	this.username = username;
    }


	public String getFamilyName()
	{
		return this.familyName;
	}

	public void setFamilyName(String familyName)
	{
		this.familyName = familyName;
	}


	public String getMail()
	{
		return this.mail;
	}

	public void setMail(String mail)
	{
		this.mail = mail;
	}


 	public String getTelephoneNumber()
 	{
 		return this.telephoneNumber;
 	}

 	public void setTelephoneNumber(String telephoneNumber)
 	{
 		this.telephoneNumber = telephoneNumber;
 	}


	
	/* Constructors */
	
	public AKKAUser(){}

}
