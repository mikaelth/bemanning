package  se.uu.ebc.bemanning.vo;

import java.util.Set;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import se.uu.ebc.bemanning.entity.CourseGrant;
import se.uu.ebc.bemanning.enums.GrantType;

public class CourseGrantVO {

    private Long id;

	private Long courseInstanceId;
	private Long debitUnitId;
	private Long departmentId;

    private Integer amount;
    private GrantType type;
    private String note;

	@JsonInclude(Include.NON_NULL)
    private Date setDate;


	/* Setters and getters */


    public Long getId()
    {
    	return this.id;
    }

    public void setId(Long id)
    {
    	this.id = id;
    }


	public Long getCourseInstanceId()
	{
		return this.courseInstanceId;
	}

	public void setCourseInstanceId(Long courseInstanceId)
	{
		this.courseInstanceId = courseInstanceId;
	}


	public Long getDebitUnitId()
	{
		return this.debitUnitId;
	}

	public void setDebitUnitId(Long debitUnitId)
	{
		this.debitUnitId = debitUnitId;
	}


	public Long getDepartmentId()
	{
		return this.departmentId;
	}

	public void setDepartmentId(Long departmentId)
	{
		this.departmentId = departmentId;
	}


    public Integer getAmount()
    {
    	return this.amount;
    }

    public void setAmount(Integer amount)
    {
    	this.amount = amount;
    }


    public GrantType getType()
    {
    	return this.type;
    }

    public void setType(GrantType type)
    {
    	this.type = type;
    }


    public String getNote()
    {
    	return this.note;
    }

    public void setNote(String note)
    {
    	this.note = note;
    }


    public Date getSetDate()
    {
    	return this.setDate;
    }

    public void setSetDate(Date setDate)
    {
    	this.setDate = setDate;
    }



	/* Constructors */
	
	public CourseGrantVO(){}
	
	public CourseGrantVO(CourseGrant xe) {

		this.id = xe.getId();

		this.courseInstanceId = xe.getCourseInstance().getId();
		this.debitUnitId = xe.getDebitUnit().getId();
		this.departmentId = xe.getDepartment().getId();

		this.amount = xe.getAmount();
		this.type = xe.getType();
		this.note = xe.getNote();
		this.setDate = xe.getSetDate();	

	}
}