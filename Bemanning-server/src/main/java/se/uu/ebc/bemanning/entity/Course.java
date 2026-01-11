package se.uu.ebc.bemanning.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "COURSE")
@Getter
@Setter
//@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode(callSuper = true)
@Slf4j
public class Course extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    
    @OneToMany(mappedBy = "course")
    private Set<CourseInstance> courseInstances;
    
    @Column(name = "CODE", length = 255)
    private String code;
    
    @Column(name = "SE_NAME", length = 255)
    @NotNull
    private String seName;
    
    @Column(name = "EN_NAME", length = 255)
    private String enName;
    
    @Column(name = "COURSE_GROUP", length = 255)
    private String courseGroup;
    
    @Column(name = "PERIOD", length = 255)
    private String period;
    
    @Column(name = "NOTE", length = 255)
    private String note;
    
    @Column(name = "CREDITS", precision = 12)
    private Float credits;
    
    @Column(name = "EFFORT_ADMIN")
    private Integer effortAdmin = 0;
    
    @Column(name = "EFFORT_DEVELOPMENT")
    private Integer effortDevelopment = 0;
    
    @Column(name = "EFFORT_LECTURE")
    private Integer effortLecture = 0;
    
    @Column(name = "EFFORT_PRACTICAL")
    private Integer effortPractical = 0;
    
    @Column(name = "EFFORT_EXCURSION")
    private Integer effortExcursion = 0;
    
    @Column(name = "EFFORT_SEMINAR")
    private Integer effortSeminar = 0;
    
/* 
	@ManyToOne
    @JoinColumn(name = "PRIM_EXAM_FK")
	private Person primaryExaminer;

	@ManyToOne
    @JoinColumn(name = "SEC_EXAM_FK")
	private Person secondaryExaminer;
 */
    
}
