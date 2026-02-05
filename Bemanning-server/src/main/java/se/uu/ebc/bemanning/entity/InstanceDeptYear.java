package se.uu.ebc.bemanning.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Table;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import se.uu.ebc.bemanning.entity.courseinstance.CourseInstance;

//import se.uu.ebc.bemanning.enums.EmploymentType;

@Entity
@Table(name = "CI_DEPT_YEAR")
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class InstanceDeptYear {

 	@EmbeddedId
 	@Getter
 	@Setter 
 	private InstYearId id;

    @ManyToOne
    @MapsId("courseInstanceId")
    @JoinColumn(name = "COURSE_INSTANCE_FK")
    @Getter
    @Setter
    CourseInstance courseInstance;

    @ManyToOne
    @MapsId("yearlyStaffingId")
    @JoinColumn(name = "YEARLY_STAFFING_FK")
    @Getter
    @Setter
    YearlyStaffing yearlyStaffing;



 	@Getter
 	@Setter 
    @Column(name = "PERCENTAGE")
 	private Float percentage;


	/* Computed properties*/


	/* Constructors */
	
	
	@Embeddable
	@NoArgsConstructor
	@AllArgsConstructor
	static class InstYearId implements Serializable {

		@Column(name = "COURSE_INSTANCE_FK")
		@Getter
		@Setter
		protected Long courseInstanceId;

		@Column(name = "YEARLY_STAFFING_FK")
		@Getter
		@Setter
		protected Long yearlyStaffingId;


		
	}

}
