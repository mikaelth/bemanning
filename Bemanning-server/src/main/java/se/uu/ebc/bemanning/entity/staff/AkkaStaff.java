package se.uu.ebc.bemanning.entity.staff;

import java.util.Set;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import se.uu.ebc.bemanning.enums.EmploymentType;

@Entity
@Getter
@Setter
//@Builder(toBuilder = true)
@NoArgsConstructor
//@AllArgsConstructor
//@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("AKKA")
@Slf4j
public class AkkaStaff extends Staff {

    @Column(name = "EMPLOYEENUMBER", length = 255, unique = true)
	private String employeeNumber;
    
    /* Public methods */
    
    
}
