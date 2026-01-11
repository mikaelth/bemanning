package se.uu.ebc.bemanning.entity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import java.util.Optional;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "PROGRESS")
@Getter
@Setter
//@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode(callSuper = true)
@Slf4j
public class Progress  extends Auditable {

	private static final float MONTH_IN_DAYS = (365.0f/12.0f);

    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "PH_D_POSITION_FK")
	private PhDPosition phdPosition;

	    
    @Column(name = "DATE")
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime date;
    
    @Column(name = "ACTIVITY", precision = 12)
    private Float activity;
    
    @Column(name = "PROJECT_FRACTION", precision = 12)
    private Float projectFraction;
    
    @Column(name = "GU_FRACTION", precision = 12)
    private Float guFraction;
    
    @Column(name = "TO_ECO_SYS")
    @NotNull
    private boolean toEcoSys = false;
    
    @Column(name = "REMAINING_MONTHS", precision = 12)
    private Float remainingMonths;
    
    @Column(name = "TO_UP_DOK")
    @NotNull
    private boolean toUpDok = false;
    
    @Column(name = "NOTE", length = 255)
    private String note;
    
    @Column(name = "ADDED_MONTHS", precision = 12)
    private Float addedMonths;

    @Transient
    private Float monthsAtStart;
    
    
    public int compareTo(Progress o)
    {
        return -(this.getDate().compareTo(o.getDate()));
    }

	public Float calcRemainMonths()
	{
		return this.getPhdPosition().remainingProjectTime(this.getDate(), true);
	}
    
	public Float theMonthsAtBeginning () {
		if (monthsAtStart == null) {
			return remainingMonths;
		} else {
			return monthsAtStart;
		}
	}
	
    public Optional<Float> monthsAtStart() {
    	return Optional.ofNullable(remainingMonths);
    }
    
    public Float phdTimeSlotUsed (LocalDateTime endDate) 
    {
		log.debug("Activity " + activity.toString() + ", project fraction " + projectFraction.toString() );
		log.debug("Elapsed time " + ChronoUnit.DAYS.between(date,endDate)/MONTH_IN_DAYS );
       	return ChronoUnit.DAYS.between(this.date,endDate)/MONTH_IN_DAYS * activity * projectFraction;
    }

}
