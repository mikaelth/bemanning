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
import jakarta.persistence.CascadeType;

import java.util.Set;

import se.uu.ebc.bemanning.entity.courseinstance.CourseInstance;
import se.uu.ebc.bemanning.entity.staff.Staff;
import se.uu.ebc.bemanning.entity.OrganisationUnit;
import se.uu.ebc.bemanning.enums.EmploymentType;
import se.uu.ebc.bemanning.enums.ActivityType;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@Setter
//@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("MODERN")
@Slf4j
public class CourseStaffingModern extends CourseStaffing {

	private final static int LECTURE_HOUR_COST = 1285;

    private static final float EXCURSION_FACTOR = 1.5f;
    private static final float PRACTICAL_FACTOR = 2.0f;
    private static final float PORFESSOR_LECTURE_FACTOR = 4.0f;
    private static final float STUDENT_LECTURE_FACTOR = 8.0f;

    @OneToOne(mappedBy = "courseStaffing")
    private AssignmentPlan plan;

    @OneToOne(mappedBy = "courseStaffing",cascade = CascadeType.ALL)
    private AssignmentTE te;

    @OneToOne(mappedBy = "courseStaffing")
    private AssignmentOutcome outcome;

//     @Column(name = "NOTE", length = 255)
//     private String note;

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
		boolean updated = false;
		if (te == null) {
			te = new AssignmentTE();
			te.setHoursAdmin(Float.valueOf(0.0f));
			te.setHoursDevelopment(Float.valueOf(0.0f));
			te.setHoursLecture(Float.valueOf(0.0f));
			te.setHoursPractical(Float.valueOf(0.0f));
			te.setHoursSeminar(Float.valueOf(0.0f));
			te.setHoursExcursion(Float.valueOf(0.0f));
			te.setCourseStaffing(this);
		};

		Float item = switch (actType) {
			case ActivityType.LECTURE -> te.getHoursLecture();
			case ActivityType.PRACTICAL -> te.getHoursPractical();
			case ActivityType.EXCURSION -> te.getHoursExcursion();
			case ActivityType.SEMINAR -> te.getHoursSeminar();
			default -> 0.0f;
		};

		if (item == 0 || replace) {
			switch (actType) {
				case ActivityType.LECTURE -> te.setHoursLecture(duration);
				case ActivityType.PRACTICAL -> te.setHoursPractical(duration);
				case ActivityType.EXCURSION -> te.setHoursExcursion(duration);
				case ActivityType.SEMINAR -> te.setHoursSeminar(duration);
			};
			updated = true;
		}

		return updated;
	}


    @Override
    public float getTotalHours() {
    	return plan.getTotalHours();
    }

    @Override
    public float getPlainTeachingHours() {
    	return plan.getPlainTeachingHours();
    }

    public Float getHoursAdmin(){return plan.getHoursAdmin();};
    public Float getHoursDevelopment(){return plan.getHoursDevelopment();};
    public Float getHoursLecture(){return plan.getHoursLecture();};
    public Float getHoursPractical(){return plan.getHoursPractical();};
    public Float getHoursExcursion(){return plan.getHoursExcursion();};
    public Float getHoursSeminar(){return plan.getHoursSeminar();};

}
