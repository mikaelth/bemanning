package se.uu.ebc.bemanning.entity.assignment;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import se.uu.ebc.bemanning.entity.OrganisationUnit;
import se.uu.ebc.bemanning.enums.EmploymentType;
import se.uu.ebc.bemanning.entity.staff.Staff;
import se.uu.ebc.bemanning.entity.courseinstance.CourseInstance;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@Setter
//@Builder(toBuilder = true)
//@NoArgsConstructor
//@AllArgsConstructor
//@EqualsAndHashCode(callSuper = true)
@Slf4j
@DiscriminatorValue("LEGACY")
public class AssignmentLegacy extends Assignment {
    

    @ManyToOne
    @NotNull
    @JoinColumn(name = "STAFF_FK")
    private Staff staff;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "COURSE_INSTANCE_FK")
    private CourseInstance courseInstance;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "DEPARTMENT_FK")
    private OrganisationUnit assigningDept;
    
    
    /* Public methods */
    

}
