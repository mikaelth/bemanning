package se.uu.ebc.bemanning.ldap.model;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.odm.annotations.DnAttribute;
//import javax.naming.Name;
import module java.naming;
import java.util.StringJoiner;

import lombok.Data;
/*
@Entry(
  base = "cn=People,dc=uu,dc=se",
  objectClasses = { "person", "inetOrgPerson", "top", "uuAKKAperson", "eduPerson" })
*/

@Entry(base = "ou=people", objectClasses = {"person", "top"})

@Data
public final class StaffAkka {

    @Id
    private Name dn;

    private @Attribute(name = "employeeNumber")
    		@DnAttribute("employeeNumber") String employeeNumber;
    private @Attribute(name = "uid") String username;
	private @Attribute(name = "cn") String name;
	private @Attribute(name = "sn") String familyName;
	private @Attribute(name = "givenname") String givenName;
    private @Attribute(name = "title") String title;
    private @Attribute(name = "ou") String department;
    private @Attribute(name = "telephoneNumber") String phone;
    private @Attribute(name = "mail") String mail;
    private @Attribute(name = "department") String fullDepartment;
	private @Attribute(name = "norEduPersonNIN") String pNIN;

	// Business methods

	public String getSortingName () {
		return familyName == null ? employeeNumber : familyName + ", " + givenName;
	}

	public String getDepartment() {
		return department == null ? "MISSING" : department;
	}

	public String getFullDepartment() {
		return fullDepartment == null ? "MISSING" : fullDepartment;
	}

	public String getNameAndOu () {

		StringJoiner staffJoiner = new StringJoiner(", ");

		staffJoiner.add(name);
		staffJoiner.add(title);
		staffJoiner.add(fullDepartment);

		return staffJoiner.toString();
	}

	public String getNameAndContact() {
		StringJoiner staffJoiner = new StringJoiner(", ");

		staffJoiner.add(name);
		staffJoiner.add(mail);
		staffJoiner.add(phone);

		return staffJoiner.toString();


	}


 }
