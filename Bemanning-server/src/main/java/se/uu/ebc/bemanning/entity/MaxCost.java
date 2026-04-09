package se.uu.ebc.bemanning.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Embeddable;

import org.hibernate.annotations.Formula;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import se.uu.ebc.bemanning.enums.EmploymentType;

@Entity
@Table(name = "MAX_COST")
@Slf4j
public class MaxCost {

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
		log.debug("getMaxHourlyCharge " + id.getPosition() + " - " + id.getYear() +": " + maxHourlyCharge);
		return this.maxHourlyCharge;
	}

	/* Constructors */

	public MaxCost() {}

	public MaxCost(EmploymentType type, String year) {
		id = new CostId(year, type);
	}


	@Embeddable
	@Setter
	@Getter
	static class CostId implements Serializable {
		@Column(name = "STAFFYEAR")
		private String year;

		@Enumerated(EnumType.STRING)
		@Column(name = "STAFFPOSITION")
		private EmploymentType position;

/*
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
 */


		public CostId() {}
		public CostId(String year, EmploymentType position){
			this.year = year;
			this.position = position;
		}

		@Override
		public boolean equals(Object o) {
			if (o == this)
				return true;
			if (!(o instanceof CostId))
				return false;
			CostId other = (CostId) o;
			boolean yearEquals = (this.year == null && other.year == null)
			  || (this.year != null && this.year.equals(other.year));
			boolean posEquals = (this.position == null && other.position == null)
			  || (this.position != null && this.position.equals(other.position));
			return yearEquals && posEquals;
		}

		@Override
		public final int hashCode() {
			int result = 17;
			if (year != null) {
				result = 31 * result + year.hashCode();
			}
			if (position != null) {
				result = 31 * result + position.hashCode();
			}
			return result;
		}


	}

}
