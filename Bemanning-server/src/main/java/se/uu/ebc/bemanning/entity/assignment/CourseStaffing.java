package se.uu.ebc.bemanning.entity.assignment;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Embedded;

import java.util.Set;

import jakarta.persistence.DiscriminatorColumn;
import se.uu.ebc.bemanning.entity.Auditable;
import se.uu.ebc.bemanning.entity.OrganisationUnit;
import se.uu.ebc.bemanning.entity.courseinstance.CourseInstance;
import se.uu.ebc.bemanning.entity.staff.Staff;
import se.uu.ebc.bemanning.enums.EmploymentType;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "COURSE_STAFFING")
@Getter
@Setter
//@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode(callSuper = true)
@DiscriminatorColumn(name="STAFFING_KIND", discriminatorType = DiscriminatorType.STRING)
@Slf4j
public abstract class CourseStaffing extends Auditable {

	private final static int LECTURE_HOUR_COST = 1285;

    private static final float EXCURSION_FACTOR = 1.5f;
    private static final float PRACTICAL_FACTOR = 2.0f;
    private static final float PORFESSOR_LECTURE_FACTOR = 4.0f;
    private static final float STUDENT_LECTURE_FACTOR = 8.0f;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOTE", length = 255)
    private String note;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "DEPARTMENT_FK")
    private OrganisationUnit assigningDept;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "STAFF_FK")
    private Staff staff;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "COURSE_INSTANCE_FK")
    private CourseInstance courseInstance;
  
  
    /* Public methods */

/*
    public float getAssignmentCost()
    {
        float cost = 0.0f;

		if (getStaff().getPosition() == EmploymentType.Timarvoderad) {
			cost = LECTURE_HOUR_COST * this.getPlainTeachingHours();
		} else {
			cost = getStaff().getHourlyCost() * this.getTotalHours(getStaff().getPosition());
		}

		return cost;

    }

    public float getTotalHours()
    {
    	return getTotalHours(staff.getPosition());
    }

    public float getTotalHours(EmploymentType employment)
    {

// 		logger.debug(hoursAdmin + ", " + hoursDevelopment + ", " + hoursSeminar + ", " + hoursExcursion +", " + hoursPractical+", "+ hoursLecture);
// 		logger.debug(hoursAdmin + ", " + hoursDevelopment + ", " + hoursSeminar + ", " + hoursExcursion*EXCURSION_FACTOR +", " + hoursPractical*PRACTICAL_FACTOR+", "+ (employment.compareTo(EmploymentType.Doktorand) == 0 ? hoursLecture*STUDENT_LECTURE_FACTOR : hoursLecture*PORFESSOR_LECTURE_FACTOR));

		try {
			return
				hoursAdmin +
				hoursDevelopment +
				hoursSeminar +
				hoursExcursion*EXCURSION_FACTOR +
				hoursPractical*PRACTICAL_FACTOR +
				(employment.compareTo(EmploymentType.Doktorand) == 0 ? hoursLecture*STUDENT_LECTURE_FACTOR : hoursLecture*PORFESSOR_LECTURE_FACTOR);
			} catch (Exception e) {
				log.error("getTotalHours caught a pesky exception " + e +", " + e.getCause());
				return 0.0f;
			}
    }

    public float getPlainTeachingHours()
    {


        return
        	hoursSeminar +
         	hoursExcursion +
			hoursPractical +
        	hoursLecture;
    }
 */

    public abstract float getTotalHours();

    public abstract float getPlainTeachingHours();

//   public abstract CourseInstance getCourseInstance();

}
