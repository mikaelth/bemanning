package se.uu.ebc.bemanning.entity.utils;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import se.uu.ebc.bemanning.entity.Auditable;
import se.uu.ebc.bemanning.entity.courseinstance.CourseInstance;
import se.uu.ebc.bemanning.enums.ActivityType;


import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "TE_ACTIVITY_LOOKUP",indexes = @Index(columnList = "teText"))
@Getter
@Setter
//@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode(callSuper = true)
@Slf4j
public class TEActivity  extends Auditable {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "TE_ACTIVITY", length = 255)
    private String teText;

    @Enumerated(EnumType.STRING)    
    @Column(name = "BP_ACTIVITY")
    private ActivityType bpActivity;

}