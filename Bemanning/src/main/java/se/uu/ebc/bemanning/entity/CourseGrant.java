package  se.uu.ebc.bemanning.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import se.uu.ebc.bemanning.entity.CourseGrant;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Table(name = "COURSE_GRANT")
public class CourseGrant {
    
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

    
    @ManyToOne
    @NotNull
    @JoinColumn(name = "COURSE_INSTANCE_FK")
	private CourseInstance courseInstance;

	public CourseInstance getCourseInstance()
	{
		return this.courseInstance;
	}

	public void setCourseInstance(CourseInstance courseInstance)
	{
		this.courseInstance = courseInstance;
	}

    @ManyToOne
    @NotNull
    @JoinColumn(name = "DEBIT_UNIT_FK")
	private OrganisationUnit debitUnit;

	public OrganisationUnit getDebitUnit()
	{
		return this.debitUnit;
	}

	public void setDebitUnit(OrganisationUnit debitUnit)
	{
		this.debitUnit = debitUnit;
	}


    @ManyToOne
    @NotNull
    @JoinColumn(name = "DEPARTMENT_FK")
	private OrganisationUnit department;

	public OrganisationUnit getDepartment()
	{
		return this.department;
	}

	public void setDepartment(OrganisationUnit department)
	{
		this.department = department;
	}

    
    @Column(name = "AMOUNT")
    private Integer amount;
    
    @Column(name = "TYPE", length = 255)
    @NotNull
    private String type;
    
    @Column(name = "NOTE", length = 255)
    private String note;
    
    @Column(name = "SET_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "MM")
    private Date setDate;
    
    public Integer getAmount() {
        return amount;
    }
    
    public void setAmount(Integer amount) {
        this.amount = amount;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    public Date getSetDate() {
        return setDate;
    }
    
    public void setSetDate(Date setDate) {
        this.setDate = setDate;
    }
    
}
