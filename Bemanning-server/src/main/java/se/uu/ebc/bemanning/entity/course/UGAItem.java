package se.uu.ebc.bemanning.entity.course;

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
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;

import se.uu.ebc.bemanning.entity.Auditable;
import se.uu.ebc.bemanning.entity.courseinstance.CourseInstance;

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
@DiscriminatorColumn(name="UGA_KIND", discriminatorType = DiscriminatorType.STRING)
public abstract class UGAItem extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;  
    
    @Column(name = "SE_NAME", length = 255)
//    @NotNull
    private String seName;
    
    @Column(name = "EN_NAME", length = 255)
    private String enName;
     
    @Column(name = "NOTE", length = 255)
    private String note;

    @OneToMany(mappedBy = "course")
    private Set<CourseInstance> courseInstances;
    

    public abstract String getFullCourseNameSv();

}

