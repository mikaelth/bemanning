package se.uu.ebc.bemanning.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.Embedded;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "YEARLYSTAFFING")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class YearlyStaffing extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    

    @Column(name = "DEPARTMENT_ABBREVIATION", length = 20)
    private String dept;
    
    @Column(name = "YEAR", length = 4)
    @NotNull
    private String year;
    
	@OneToMany(mappedBy = "yearlyStaffing")
	Set<InstanceDeptYear> courseInstances;


	@Embedded
	private FactorRecord teachingFactors;
    
	

}
