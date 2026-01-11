package  se.uu.ebc.bemanning.vo;

import  se.uu.ebc.bemanning.entity.Course;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@Slf4j
public class CourseVO {

    private Long id;

    private String code;
    
    private String seName;
    
    private String enName;
    
    private String courseGroup;
    
    private String period;

    private String note;
    
    private Float credits;
    
/* 
    private Integer effortAdmin;
    
    private Integer effortDevelopment;
    
    private Integer effortLecture;
    
    private Integer effortPractical;
    
    private Integer effortExcursion;
    
    private Integer effortSeminar;
 */
    
    

	public CourseVO(Course c) {
		this.id = c.getId();
		this.code = c.getCode();
		this.seName = c.getSeName();
		this.enName = c.getEnName();
		this.courseGroup = c.getCourseGroup();
		this.period = c.getPeriod();
		this.note = c.getNote();
		this.credits = c.getCredits();
	}
    
}
