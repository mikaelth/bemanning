package se.uu.ebc.bemanning.entity;

import java.util.Set;
import java.util.HashSet;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import jakarta.validation.constraints.NotNull;

import se.uu.ebc.bemanning.enums.UserRoleType;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "PERSON")
@Getter
@Setter
//@Builder(toBuilder = true)
//@NoArgsConstructor
//@AllArgsConstructor
//@EqualsAndHashCode(callSuper = true)
@Slf4j
public class Person extends Auditable {
	    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="native")
    @Column(name = "ID")
    private Long id;

    
    @OneToOne(mappedBy = "person")
    private PhDPosition phDPosition;
    
    @OneToMany(mappedBy = "person")
    private Set<Staff> staff;
    
    @Column(name = "GIVEN_NAME", length = 255)
    @NotNull
    private String givenName;
    
    @Column(name = "FAMILY_NAME", length = 255)
    @NotNull
    private String familyName;
    
    @Column(name = "FAMILY_FIRST")
    @NotNull
    private boolean familyFirst;
    
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
 
/* 
    @OneToMany(mappedBy = "primaryExaminer")
    private Set<Course> primExamCourses;

    @OneToMany(mappedBy = "secondaryExaminer")
    private Set<Course> secExamCourses;
    
    
 */
 
	/* Constructors */
	
	public Person() {
//		userRoles = new HashSet<UserRole>();
		staff = new HashSet<Staff>();
		userRoles.add(UserRoleType.Staff);
	}
	
    /* Custom business methods */

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
