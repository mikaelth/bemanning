package se.uu.ebc.bemanning.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import se.uu.ebc.bemanning.entity.courseinstance.CourseInstance;
import se.uu.ebc.bemanning.enums.GrantType;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "COURSE_GRANT")
@Getter
@Setter
//@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode(callSuper = true)
@Slf4j
public class CourseGrant  extends Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "COURSE_INSTANCE_FK")
    private CourseInstance courseInstance;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "DEBIT_UNIT_FK")
    private OrganisationUnit debitUnit;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "DEPARTMENT_FK")
    private OrganisationUnit department;
    
    @Column(name = "AMOUNT")
    private Integer amount;
    
    @Enumerated(EnumType.STRING)    
    @Column(name = "TYPE", length = 255)
    @NotNull
    private GrantType type;
    
    @Column(name = "NOTE", length = 255)
    private String note;
    
    @Column(name = "SET_DATE")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime setDate;
    
}
