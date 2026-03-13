package se.uu.ebc.bemanning.entity.assignment;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
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

import se.uu.ebc.bemanning.entity.courseinstance.CourseInstance;
import se.uu.ebc.bemanning.entity.staff.Staff;
import se.uu.ebc.bemanning.enums.EmploymentType;
import se.uu.ebc.bemanning.enums.ActivityType;
import se.uu.ebc.bemanning.enums.FactorCategory;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@Setter
//@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("LEGACY")
@Slf4j
public class CourseStaffingLegacy extends CourseStaffing {

	private final static int LECTURE_HOUR_COST = 1285;

    private static final float EXCURSION_FACTOR = 1.5f;
    private static final float PRACTICAL_FACTOR = 2.0f;
    private static final float PORFESSOR_LECTURE_FACTOR = 4.0f;
    private static final float STUDENT_LECTURE_FACTOR = 8.0f;

  

    @OneToOne(mappedBy = "courseStaffing")
    @NotNull
    private AssignmentLegacy legacy;


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

    @Override
	public boolean updateTEAssignment (ActivityType actType, Float duration, boolean replace) {
		return false;
	}
	
    @Override
    public float getTotalHours() {
    	return legacy.getTotalHours();
    }

    @Override
    public float getPlainTeachingHours() {
    	return legacy.getPlainTeachingHours();
    }

    // @Override
    // public CourseInstance getCourseInstance() {
    // 	return legacy.getCourseInstance();
    // }


    public Float getHoursAdmin(){return legacy.getHoursAdmin();};
    public Float getHoursDevelopment(){return legacy.getHoursDevelopment();};
    public Float getHoursLecture(){return legacy.getHoursLecture();};
    public Float getHoursPractical(){return legacy.getHoursPractical();};
    public Float getHoursExcursion(){return legacy.getHoursExcursion();};
    public Float getHoursSeminar(){return legacy.getHoursSeminar();};
  

    public Float[] getArrHoursLecture() {
    	Float[] hours = new Float[3];
    	hours[0] = legacy != null ? legacy.getHoursLecture() : 0.0f;
    	hours[1] = 0.0f;
    	hours[2] =  0.0f;
    	
    	return hours;
    }
    public Float[] getArrHoursPractical() {
    	Float[] hours = new Float[3];
    	hours[0] = legacy != null ? legacy.getHoursPractical() : 0.0f;
    	hours[1] = 0.0f;
    	hours[2] =  0.0f;
    	
    	return hours;
    }

      public Float[] getArrHours(FactorCategory cat) {
    	Float[] hours = new Float[3];
		hours[0] = switch (cat) {
			case FactorCategory.PRACTICAL -> legacy.getHoursPractical();
			case FactorCategory.LECTURE -> legacy.getHoursLecture();
			case FactorCategory.SEMINAR -> legacy.getHoursSeminar();
			case FactorCategory.EXCURSION -> legacy.getHoursExcursion();
			case FactorCategory.DEVELOPMENT -> legacy.getHoursDevelopment();
			case FactorCategory.ADMIN -> legacy.getHoursAdmin();
		};
		hours[1] = 0.0f;
		hours[2] = 0.0f;
		
      	return hours;
    }

}
