package se.uu.ebc.ldap;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;

@Entry(base = "cn=People", objectClasses = { "person", "inetOrgPerson", "top", "eduPerson", "uuAKKAperson" })
public class AKKAUser {

    @Id
    private Name dn;
    
/* 
    @Attribute(name = "cn") 
    private String username;
 */	

	@Attribute(name="sn")
	private String familyName;


	@Attribute(name="cn")
//	@DnAttribute(value="cn",index=1)
	private String fullName;

	@Attribute(name="uid")
	private String uid;

	@Attribute(name="mail")
	private String mail;

	@Attribute(name="telephoneNumber")
 	private String telephoneNumber;



	/* Setters and getters */
	

    public Name getDn()
    {
    	return this.dn;
    }

    public void setId(Name dn)
    {
    	this.dn = dn;
    }


/* 
    public String getUsername()
    {
    	return this.username;
    }

    public void setUsername(String username)
    {
    	this.username = username;
    }
 */

	public String getFamilyName()
	{
		return this.familyName;
	}

	public void setFamilyName(String familyName)
	{
		this.familyName = familyName;
	}


	public String getUid()
	{
		return this.uid;
	}

	public void setUid(String uid)
	{
		this.uid = uid;
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

	public AKKAUser(Name dn){
		this.dn = dn;
	}

}
