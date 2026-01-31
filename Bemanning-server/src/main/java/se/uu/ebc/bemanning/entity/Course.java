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
import jakarta.persistence.DiscriminatorValue;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
//@Table(name = "COURSE")
@Getter
@Setter
//@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("COURSE")
@Slf4j
public class Course extends UGAItem {

    
    @OneToMany(mappedBy = "course")
    private Set<CourseInstance> courseInstances;
    
    @Column(name = "CODE", length = 255, unique = true)
    private String code;
        
    @Column(name = "COURSE_GROUP", length = 255)
    private String courseGroup;
    
    @Column(name = "CREDITS", precision = 12)
    private Float credits;

/*     
  
	@ManyToOne
    @JoinColumn(name = "PRIM_EXAM_FK")
	private Person primaryExaminer;

	@ManyToOne
    @JoinColumn(name = "SEC_EXAM_FK")
	private Person secondaryExaminer;
 */
    
}
