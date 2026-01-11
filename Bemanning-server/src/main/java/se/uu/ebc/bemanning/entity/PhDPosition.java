package se.uu.ebc.bemanning.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;
import java.util.stream.Gatherers;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.UniqueConstraint;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import se.uu.ebc.bemanning.enums.EmploymentType;

@Entity
@Table(name = "PH_D_POSITION", uniqueConstraints= @UniqueConstraint(columnNames={"PERSON_FK"}))
@Getter
@Setter
//@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode(callSuper = true)
@Slf4j
public class PhDPosition  extends Auditable {

	private static final float MONTH_IN_MILLS = 24*60*60*1000*(365.0f/12.0f);
	private static final float HOUR_IN_MILLS = 60*60*1000.0f;
	private static final float MONTH_IN_DAYS = (365.0f/12.0f);
	private static final float DAY_IN_HOURS = (1700.0f/365.0f);
	private static final float REMAIN_AT_START = 48.0f;
	private static final float REMAIN_AT_HALF = 24.0f;
	private static final float REMAIN_AT_80 = 0.2f*48.0f;
	private static final float REMAIN_AT_75 = 0.25f*48.0f;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @OneToOne
    @NotNull
    @JoinColumn(name = "PERSON_FK")
	private Person person;

    
	@OrderBy("date ASC")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "phdPosition")
    private List<Progress> progresses = new ArrayList<Progress>();
    
    @Column(name = "START")
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime start;
    
    @Column(name = "DISSERTATION")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dissertation;
    
    @Column(name = "NOTE", length = 255)
    private String note;
    
    @Column(name = "INACTIVE")
    private boolean inactive;


    public List<Progress> getProgresses() 
    {
		Collections.sort(progresses, new Comparator<Progress>() {
			@Override
			public int compare(Progress lhs, Progress rhs) {
				// -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
				return lhs.getDate().isAfter(rhs.getDate()) ? -1 : (lhs.getDate().isBefore(rhs.getDate())) ? 1 : 0;
			}
		});
        return progresses;
    }
    
    public LocalDateTime getStart() 
    {
		LocalDateTime start = this.start;
		if (progresses.size() > 0){
			start = this.getProgresses().get(progresses.size()-1).getDate();
		}
        return start;
    }


	/* Public methods */
	
	public Float usedISPDate(LocalDateTime ispDate) 
	{
		String workingYear = String.valueOf(ispDate.getYear());
		
		float remainTime = remainingProjectTime(ispDate, false);
		float usedTime = remainTime < 0.0f ? REMAIN_AT_START : REMAIN_AT_START - remainTime;

		float percentGU = this.periodGU(workingYear, ispDate);

		LocalDate yearStart = LocalDate.of(ispDate.getYear(), 1, 1);
		int days = (int) ChronoUnit.DAYS.between(yearStart, ispDate.toLocalDate());

		int daysPlannedGU = Math.round(days * percentGU);
		int daysWorkedGU = 0;

		List<Staff> positionYear = this.person.getStaff().stream()
			.filter(s -> s.getYear().equals(workingYear))
			.filter(s -> s.getPosition() == EmploymentType.Doktorand)
			.collect(Collectors.toList());

		if (positionYear.size() == 1) {
			Staff s = positionYear.get(0);
			daysWorkedGU = Math.round((s.getTotalHours(ispDate) + s.getIb())/DAY_IN_HOURS);
			usedTime -= (daysWorkedGU - daysPlannedGU)/MONTH_IN_DAYS;
		} else {
			log.error("Number of Staff positions incorrect for " + this.person.getName());
		}
		
		log.debug(this.person.getName() + " planned " + daysPlannedGU + ", days worked " +  daysWorkedGU + ", used time " + usedTime);

		return usedTime;

	}

	public LocalDateTime predictedFinishDate()
	{
		log.debug("MTh predictedFinishDate ");
		
		if (this.progresses.size() > 0) {
			Progress latestEntry = this.getProgresses().iterator().next();
			
			log.debug("Latest entry is " + latestEntry.getDate().toString());
			
//			float remainAtLatest = remainingProjectTime(latestEntry.getDate(), false);
			float remainAtLatest = this.progresses.size() == 1 ? latestEntry.getRemainingMonths() : remainingProjectTime(latestEntry.getDate(), false);

			float remainPredInDays = remainAtLatest * MONTH_IN_DAYS / (latestEntry.getActivity() * latestEntry.getProjectFraction());
				
			LocalDateTime endDate = latestEntry.getDate().plusDays(Math.round(remainPredInDays));

			log.debug("MTh predictedFinishDate is " + endDate);
		
			return endDate;
		} else {
			return LocalDateTime.now();
		}
	}
	
 	public LocalDateTime predictedHalfTime()
	{
		return predictDate(REMAIN_AT_HALF);
	}

	public LocalDateTime predicted80Percent()
	{
		return predictDate(REMAIN_AT_80);
	}

	public LocalDateTime predicted75Percent()
	{
		return predictDate(REMAIN_AT_75);
	}

    public Float yearlyGU(String year) 
    {
    	return periodGU(year, LocalDateTime.of(Integer.parseInt(year) + 1, 1, 1, 0, 0));
    }


	private Float periodGU(String year, LocalDateTime atDate)
    {
		log.debug("MTh yearlyGU " + year);
		
		float yearFactor = 1.0f;
		LocalDateTime predFinish = this.predictedFinishDate();
		
		LocalDateTime periodBegin = LocalDateTime.of(Integer.parseInt(year), 1, 1, 0, 0);
		LocalDateTime periodEnd = atDate;
		List<Progress> slots = new ArrayList<Progress>();
		
		if (predFinish.isBefore(periodEnd)) {
			long totalDuration = ChronoUnit.MILLIS.between(periodBegin, periodEnd);
			long finishDuration = ChronoUnit.MILLIS.between(periodBegin, predFinish);
			yearFactor = (float) finishDuration / totalDuration;
			periodEnd = predFinish;
		}

		log.debug("MTh yearlyGU, begin " + periodBegin);
		log.debug("MTh yearlyGU, end " + periodEnd);
		
		for (Progress entry : this.getProgresses()) {
			log.debug("MTh yearlyGU, entry " + entry.getDate());

			if (entry.getDate().isBefore(periodEnd) && !entry.getDate().isBefore(periodBegin)) {
				slots.add(entry);
				log.debug("MTh yearlyGU, entry fits");
			} else if ((slots.isEmpty() || slots.get(slots.size()-1).getDate().isAfter(periodBegin)) && entry.getDate().isBefore(periodBegin)) {
				slots.add(entry);
				log.debug("MTh yearlyGU, entry should be added as IB");
			}
		}
		
		if (log.isDebugEnabled()) {
			for (Progress ent : slots) {
				log.debug("MTh yearlyGU " + ent.getDate());
			}
		}

		Progress next = null;
		float pGU = 0.0f;
		long totalPeriodMillis = ChronoUnit.MILLIS.between(periodBegin, periodEnd);
		
		for (Progress slot : slots) {
			if (next != null) {
				LocalDateTime slotStart = slot.getDate().isAfter(periodBegin) ? slot.getDate() : periodBegin;
				long span = ChronoUnit.MILLIS.between(slotStart, next.getDate());
				pGU += (float) span / totalPeriodMillis * slot.getActivity() * slot.getGuFraction();
				log.debug("MTh yearlyGU, diff " + span / (24*60*60*1000) + ", " + pGU);
			} else {
				LocalDateTime slotStart = slot.getDate().isAfter(periodBegin) ? slot.getDate() : periodBegin;
				long span = ChronoUnit.MILLIS.between(slotStart, periodEnd);
				pGU += (float) span / totalPeriodMillis * slot.getActivity() * slot.getGuFraction();
				log.debug("MTh yearlyGU, (first) diff " + span / (24*60*60*1000) + ", " + pGU);
			}
			next = slot;
		}
		
        return pGU * yearFactor;
    }


	public Float remainingProjectTime(LocalDateTime atDate, boolean ignoreSameDateEntry)
	{

/* 
		record AtDateSlot (LocalDateTime date) implements ActivitySlot {
			@Override
			public Float activity () {
				return 0.0f;
			}
			@Override
			public Float phdTimeSlotUsed (ActivitySlot slot) {
				return 0.0f;
			}
		}
	
		List<ActivitySlot> slots = new ArrayList<ActivitySlot>(progresses);
		//slots.addAll(progresses);
		slots.add(new AtDateSlot(atDate));
 */
		log.debug("MTh remainingProjectTime, at date " + atDate);

		record SlotRec (LocalDateTime date, Float intensity, Float addedMonths) {
			Float phdTimeSlotUsed (LocalDateTime endDate) {
		       	return ChronoUnit.DAYS.between(this.date,endDate)/MONTH_IN_DAYS * this.intensity + ( addedMonths!= null ? addedMonths : 0.0f );
			}
		};

		List<SlotRec> atDateList = new ArrayList<SlotRec>();
		atDateList.add(new SlotRec(atDate, 0.0f, 0.0f));
		
		Stream<SlotRec> workSlots = progresses
				.stream()
				.filter(slot -> slot.getDate().isBefore(atDate))
				.map(p -> new SlotRec(p.getDate(),p.getActivity()*p.getProjectFraction(), p.getAddedMonths()));
				
		log.debug("workSlots " + workSlots.toString());
	
		Float usedMonths = Stream.concat(workSlots,atDateList.stream())
.peek(slot -> log.debug("the slot " + slot.date.toString()))
				.sorted((a, b) -> b.date.compareTo(a.date))
				.gather(Gatherers.windowSliding(2))
				.map(w -> w.get(1).phdTimeSlotUsed(w.get(0).date))
				.reduce(0.0f,Float::sum);

		log.debug("Used months " + usedMonths);
		return REMAIN_AT_START-usedMonths;
	} 

 
	public Float currentRemainingProjectTime()
	{
		return remainingProjectTime(LocalDateTime.now(), false);
	}


	/* Private utility methods */
	
	private LocalDateTime predictDate(float months)
	{
	
		log.debug("MTh predictedHalfTime ");
 
/*
		record SlotRec (LocalDateTime date  start, LocalDateTime end , Float intensity, Float addedMonths, Float monthsAtStart) {
 
			Float timeUsed () {
		       	return ChronoUnit.DAYS.between(start,end)/MONTH_IN_DAYS * this.intensity + ( addedMonths!= null ? addedMonths : 0.0f );
			}

			Float timeUsed (LocalDateTime end) {
		       	return ChronoUnit.DAYS.between(this.date,end)/MONTH_IN_DAYS * this.intensity + ( addedMonths!= null ? addedMonths : 0.0f );
			}

			Float remainAtEnd() {
				return monthsAtStart-this.timeUsed();
			}

		};
 */
/* 
try {
		Object lastSlot = progresses
			.stream()
			.sorted((b, a) -> b.getDate().compareTo(a.getDate()))
//.peek(slot -> log.debug("the slot " + slot.getDate().toString()))
//.peek(slot -> log.debug("the slot " + (slot.monthsAtStart().isPresent() ? "not null" : "null")))
			.gather(Gatherers.windowSliding(2))
//.peek(w -> log.debug( (w.get(0).monthsAtStart().isPresent() ? w.get(0).getRemainingMonths().toString() : "null") + ", " + (w.get(1).monthsAtStart().isPresent() ? w.get(1).getRemainingMonths().toString() : "null")))
			.gather(Gatherer.of((state, w, downstream) -> {
					w.get(1).setMonthsAtStart( w.get(1).monthsAtStart().isPresent() ? w.get(1).getRemainingMonths() : w.get(0).theMonthsAtBeginning() - w.get(0).phdTimeSlotUsed(w.get(1).getDate()) ); 
	           		downstream.push((Progress)w.get(1));
            		return true;
        	}))
//.peek(s -> log.debug("Months at beginning: " + ((Progress)s).getDate().toString() +", " + ((Progress)s).theMonthsAtBeginning().toString()))
			.filter(s -> ((Progress)s).theMonthsAtBeginning() > months)
//.peek(s -> log.debug("Filtered, months at beginning: " + ((Progress)s).getDate().toString() +", " + ((Progress)s).theMonthsAtBeginning().toString()))
			.reduce((first, second) -> second).orElse(null);

log.debug ("The last object: " + ((Progress)lastSlot).getDate().toString());

			LocalDateTime aDate =  ((Progress)lastSlot).getDate().plusDays(Math.round( (((Progress)lastSlot).theMonthsAtBeginning() - months) * MONTH_IN_DAYS /  ( ((Progress)lastSlot).getActivity() * ((Progress)lastSlot).getProjectFraction() )));

log.debug ("Predicted date: " + aDate.toString());

} catch (Exception e) {
log.error("Caught an exception in stream, ", e);
}
 */
 
		
		if (this.progresses.size() > 0) {
			if (this.progresses.size() == 1) {
				return progresses.get(0).getDate().plusDays(Math.round( (progresses.get(0).theMonthsAtBeginning() - months) * MONTH_IN_DAYS /  ( progresses.get(0).getActivity() * progresses.get(0).getProjectFraction() )));							
			} else {
				Object lastSlot = progresses
					.stream()
					.sorted((b, a) -> b.getDate().compareTo(a.getDate()))
.peek(slot -> log.debug("369 the slot " + slot.getDate().toString()))
					.gather(Gatherers.windowSliding(2))
					.gather(Gatherer.of((state, w, downstream) -> {
							w.get(1).setMonthsAtStart( w.get(1).monthsAtStart().isPresent() ? w.get(1).getRemainingMonths() : w.get(0).theMonthsAtBeginning() - w.get(0).phdTimeSlotUsed(w.get(1).getDate()) ); 
							downstream.push( ((Progress)w.get(1)) );
							return true;
					}))
.peek(slot -> log.debug("376 the slot " + ((Progress)slot).getDate().toString()))
					.filter(s -> ((Progress)s).theMonthsAtBeginning() > months)
.peek(slot -> log.debug("378 the slot " + ((Progress)slot).getDate().toString()))
					.reduce((first, second) -> second).orElse(getProgresses().get(0)); /* get last slot; if there is no subsequent slot with remaining time > initial time, use slot with initial time */
			
				log.debug("lastSlot is null: " + (lastSlot == null ? "true" : "false"));
				log.debug ("The last object: " + ((Progress)lastSlot).getDate().toString());
		
				return  ((Progress)lastSlot).getDate().plusDays(Math.round( (((Progress)lastSlot).theMonthsAtBeginning() - months) * MONTH_IN_DAYS /  ( ((Progress)lastSlot).getActivity() * ((Progress)lastSlot).getProjectFraction() )));
		
			}
/* 
			java.util.Iterator<Progress> entryIterator = this.getProgresses().iterator();
			Progress entry = entryIterator.next();
			while (entryIterator.hasNext() && months > remainingProjectTime(entry.getDate(), false)) {
				entry = entryIterator.next();
			}
			float remainAtLatest = remainingProjectTime(entry.getDate(), false);

			float remainPredInDays = (remainAtLatest - months) * MONTH_IN_DAYS / (entry.getActivity() * entry.getProjectFraction());
				
			LocalDateTime endDate = entry.getDate().plusDays(Math.round(remainPredInDays));

			log.debug("MTh predictedHalfTime is " + endDate);
 */	
		} else {
			return LocalDateTime.now();
		}
	}

 
	private Float endYearRemainingProjectTime()
	{
		LocalDateTime endOfYear = LocalDateTime.of(LocalDate.now().getYear(), 12, 31, 23, 59, 59);
		return remainingProjectTime(endOfYear, false);
	}

	private Float endPrevYearRemainTime()
	{
		LocalDateTime endOfPrevYear = LocalDateTime.of(LocalDate.now().getYear() - 1, 12, 31, 23, 59, 59);
		return remainingProjectTime(endOfPrevYear, false);
	}


}
