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
@Getter
@Setter
//@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("OTHER")
@Slf4j
public class DeptUGA extends UGAItem {

    
    @OneToMany(mappedBy = "course")
    private Set<CourseInstance> courseInstances;
    
    
}
