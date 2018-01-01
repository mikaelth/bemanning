package se.uu.ebc.bemanning.entity;

import java.util.Set;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.OneToMany;
import  se.uu.ebc.bemanning.entity.Assignment;
import  se.uu.ebc.bemanning.entity.CourseInstance;
import  se.uu.ebc.bemanning.entity.Staff;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.EmbeddedId;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.Embeddable;

import org.hibernate.annotations.Formula;

import org.apache.log4j.Logger;

import se.uu.ebc.bemanning.enums.EmploymentType;

@Entity
@Table(name = "MAX_COST")
public class MaxCost {

   	private static Logger logger = Logger.getLogger(MaxCost.class.getName());

 	@EmbeddedId 
 	private CostId id;

 	public CostId getId()
 	{
 		return this.id;
 	}
 	public void setId(CostId id)
 	{
 		this.id = id;
 	}


	/* Computed properties*/

	@Formula("(select max(s.HOURLY_CHARGE) from staff as s where s.YEAR=staffyear AND s.POSITION=staffposition )")
	private Float maxHourlyCharge = 0.0f;

	public Float getMaxHourlyCharge()
	{
//		logger.debug("getMaxHourlyCharge " + id.getPosition() + " - " + id.getYear() +": " + maxHourlyCharge);
		return this.maxHourlyCharge;
	}

	public MaxCost() {}
	
	@Embeddable
	static class CostId implements Serializable {
		@Column(name = "STAFFYEAR")
		private String year;

		@Enumerated(EnumType.STRING)    
		@Column(name = "STAFFPOSITION")
		private EmploymentType position;


		public String getYear()
		{
			return this.year;
		}

		public void setYear(String year)
		{
			this.year = year;
		}

	
		public EmploymentType getPosition()
		{
			return this.position;
		}

		public void setPosition(EmploymentType position)
		{
			this.position = position;
		}

		
		public CostId() {}
		public CostId(String year, EmploymentType position){
			this.year = year;
			this.position = position;
		}
	}
}
