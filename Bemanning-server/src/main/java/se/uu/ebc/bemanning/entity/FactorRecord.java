package se.uu.ebc.bemanning.entity;

import jakarta.validation.constraints.*;
import jakarta.persistence.Embeddable;

/* 
import java.time.LocalDateTime;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Column;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Temporal;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.extern.slf4j.Slf4j;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Slf4j
 */
@Embeddable
public record FactorRecord (

	@Positive(message = "Factor should be positive")
	float EXCURSION_FACTOR,
	
	float PRACTICAL_FACTOR,
	float PROFESSOR_LECTURE_FACTOR, 
	float STUDENT_LECTURE_FACTOR

)	{}
