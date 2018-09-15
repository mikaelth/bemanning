package  se.uu.ebc.bemanning.entity;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import se.uu.ebc.bemanning.enums.EmploymentType;
import  se.uu.ebc.bemanning.entity.Assignment;

import org.apache.log4j.Logger;

@Entity
@Table(name = "ASSIGNMENT")
public class Assignment extends Auditable {

    private static Logger logger = Logger.getLogger(Assignment.class.getName());

	private final static int LECTURE_HOUR_COST = 1285;

    private static final float EXCURSION_FACTOR = 1.5f;
    private static final float PRACTICAL_FACTOR = 2.0f;
    private static final float PORFESSOR_LECTURE_FACTOR = 4.0f;
    private static final float STUDENT_LECTURE_FACTOR = 8.0f;
    
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
    @JoinColumn(name = "STAFF_FK")
	private Staff staff;

	public Staff getStaff()
	{
		return this.staff;
	}

	public void setStaff(Staff staff)
	{
		this.staff = staff;
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
    @JoinColumn(name = "DEPARTMENT_FK")
	private OrganisationUnit assigningDept;

    @Column(name = "HOURS_ADMIN", precision = 12)
    @NotNull
    private Float hoursAdmin;
    
    @Column(name = "HOURS_DEVELOPMENT", precision = 12)
    @NotNull
    private Float hoursDevelopment;
    
    @Column(name = "HOURS_LECTURE", precision = 12)
    @NotNull
    private Float hoursLecture;
    
    @Column(name = "HOURS_PRACTICAL", precision = 12)
    @NotNull
    private Float hoursPractical;
    
    @Column(name = "HOURS_EXCURSION", precision = 12)
    @NotNull
    private Float hoursExcursion;
    
    @Column(name = "HOURS_SEMINAR", precision = 12)
    @NotNull
    private Float hoursSeminar;
    
    @Column(name = "NOTE", length = 255)
    private String note;
    

	/* Setters and getters */
	
    public Float getHoursAdmin() {
        return hoursAdmin;
    }
    
    public void setHoursAdmin(Float hoursAdmin) {
        this.hoursAdmin = hoursAdmin;
    }
    
    public Float getHoursDevelopment() {
        return hoursDevelopment;
    }
    
    public void setHoursDevelopment(Float hoursDevelopment) {
        this.hoursDevelopment = hoursDevelopment;
    }
    
    public Float getHoursLecture() {
        return hoursLecture;
    }
    
    public void setHoursLecture(Float hoursLecture) {
        this.hoursLecture = hoursLecture;
    }
    
    public Float getHoursPractical() {
        return hoursPractical;
    }
    
    public void setHoursPractical(Float hoursPractical) {
        this.hoursPractical = hoursPractical;
    }
    
    public Float getHoursExcursion() {
        return hoursExcursion;
    }
    
    public void setHoursExcursion(Float hoursExcursion) {
        this.hoursExcursion = hoursExcursion;
    }
    
    public Float getHoursSeminar() {
        return hoursSeminar;
    }
    
    public void setHoursSeminar(Float hoursSeminar) {
        this.hoursSeminar = hoursSeminar;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
	public OrganisationUnit getAssigningDept()
	{
		return this.assigningDept;
	}

	public void setAssigningDept(OrganisationUnit assigningDept)
	{
		this.assigningDept = assigningDept;
	}


    
    
    /* Public methods */
    
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

        return 
        	hoursAdmin +
        	hoursDevelopment +
        	hoursSeminar +
         	hoursExcursion*EXCURSION_FACTOR +
			hoursPractical*PRACTICAL_FACTOR +
        	(employment.compareTo(EmploymentType.Doktorand) == 0 ? hoursLecture*STUDENT_LECTURE_FACTOR : hoursLecture*PORFESSOR_LECTURE_FACTOR);
    }

    public float getPlainTeachingHours()
    {


        return 
        	hoursSeminar +
         	hoursExcursion +
			hoursPractical +
        	hoursLecture;
    }

}
