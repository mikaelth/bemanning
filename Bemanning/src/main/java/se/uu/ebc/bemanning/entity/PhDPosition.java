package  se.uu.ebc.bemanning.entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.Collections;
import java.util.Comparator;
import java.time.Duration;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.persistence.UniqueConstraint;

import org.apache.log4j.Logger;

import org.springframework.format.annotation.DateTimeFormat;

import se.uu.ebc.bemanning.entity.PhDPosition;
import se.uu.ebc.bemanning.entity.Progress;
import se.uu.ebc.bemanning.enums.EmploymentType;

@Entity
@Table(name = "PH_D_POSITION", uniqueConstraints= @UniqueConstraint(columnNames={"PERSON_FK"}))
public class PhDPosition  extends Auditable {

    @Transient
    private static Logger logger = Logger.getLogger(PhDPosition.class.getName());

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
    
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    @OneToOne
    @NotNull
    @JoinColumn(name = "PERSON_FK")
	private Person person;

    
	@OrderBy("date ASC")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "phdPosition")
    private List<Progress> progresses = new ArrayList<Progress>();
    
    @Column(name = "START")
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "MM")
    private Date start;
    
    @Column(name = "DISSERTATION")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "MM")
    private Date dissertation;
    
    @Column(name = "NOTE", length = 255)
    private String note;
    
    @Column(name = "INACTIVE")
    private boolean inactive;


	/* Setters and getters */
	    
	public Person getPerson()
	{
		return this.person;
	}

	public void setPerson(Person person)
	{
		this.person = person;
	}

    public List<Progress> getProgresses() {

		Collections.sort(progresses, new Comparator<Progress>() {
			@Override
			public int compare(Progress lhs, Progress rhs) {
				// -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
				return lhs.getDate().after(rhs.getDate()) ? -1 : (lhs.getDate().before(rhs.getDate())) ? 1 : 0;
			}
		});

        return progresses;
    }
    
    public void setProgresses(List<Progress> progresses) {


       this.progresses = progresses;
    }
    
    public Date getStart() {
		Date start = this.start;

		if (progresses.size() > 0){
			start = this.getProgresses().get(progresses.size()-1).getDate();
		}
        return start;
    }
    
    public void setStart(Date start) {
        this.start = start;
    }
    
    public Date getDissertation() {
        return dissertation;
    }
    
    public void setDissertation(Date dissertation) {
        this.dissertation = dissertation;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    public boolean getInactive() {
        return inactive;
    }
    
    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }


	/* Public methods */
	
	public Float usedISPDate(Calendar ispDate) {
		String workingYear = String.valueOf( ispDate.get(Calendar.YEAR) );
		
		float remainTime = remainingProjectTime( ispDate.getTime(), false );
		float usedTime = remainTime < 0.0f ? REMAIN_AT_START : REMAIN_AT_START - remainTime;

		float percentGU = this.periodGU(workingYear, ispDate);

		int days = (int) Duration.between(new GregorianCalendar(ispDate.get(Calendar.YEAR),0,1).toInstant(), ispDate.toInstant()).toDays();

		int daysPlannedGU = Math.round( days * percentGU);
		int daysWorkedGU = 0;

		List<Staff> positionYear = this.person.getStaff().stream()
			.filter(s -> s.getYear().equals(workingYear))
			.filter(s -> s.getPosition() == EmploymentType.Doktorand)
			.collect(Collectors.toList());

		if (positionYear.size() == 1) {
			Staff s = positionYear.get(0);
			daysWorkedGU = Math.round((s.getTotalHours(ispDate.getTime()) + s.getIb())/DAY_IN_HOURS);
			usedTime -= (daysWorkedGU - daysPlannedGU)/MONTH_IN_DAYS;
		} else {
			logger.error("Number of Staff positions incorrect for " + this.person.getName() );
		}
		
		logger.debug(this.person.getName() + " planned " + daysPlannedGU + ", days worked " +  daysWorkedGU + ", used time " + usedTime);

		return usedTime;

	}

	public Date predictedFinishDate()
	{
		logger.debug("MTh predictedFinishDate " );
		
		if (this.progresses.size() > 0) {
			Calendar now = Calendar.getInstance();
			String thisYear = Integer.toString((now.get(Calendar.YEAR)));

			Calendar ey = Calendar.getInstance();
			ey.set(Calendar.MONTH, Calendar.DECEMBER);
			ey.set(Calendar.DATE, 31);
	
			Progress latestEntry = this.getProgresses().iterator().next();
			float remainAtLatest = remainingProjectTime(latestEntry.getDate(), false);

			float remainPredInDays = remainAtLatest*MONTH_IN_DAYS/(latestEntry.getActivity()*latestEntry.getProjectFraction()) ;
				
			Calendar endDate = Calendar.getInstance();
			endDate.setTime(latestEntry.getDate());
			endDate.add(Calendar.DAY_OF_MONTH,Math.round(remainPredInDays));

			logger.debug("MTh predictedFinishDate is "+ endDate.getTime() );
		
			return endDate.getTime();
		} else {
			return new Date();
		}
	}
	
 	public Date predictedHalfTime()
	{
		return predictDate(REMAIN_AT_HALF);
	}

	public Date predicted80Percent()
	{
		return predictDate(REMAIN_AT_80);
	}

	public Date predicted75Percent()
	{
		return predictDate(REMAIN_AT_75);
	}

    public Float yearlyGU(String year) {
    	return periodGU(year, new GregorianCalendar(Integer.parseInt(year)+1,0,1));
    }


	private Float periodGU(String year, Calendar atDate)
    {
		logger.debug("MTh yearlyGU "+year );
		
		float yearFactor = 1.0f;
		Date predFinish = this.predictedFinishDate();
		
		Calendar periodBegin = Calendar.getInstance();
		Calendar periodEnd = Calendar.getInstance();
		List<Progress> slots = new ArrayList<Progress>();
		
		periodBegin.clear();
		periodEnd.clear();
		periodBegin.set( Integer.parseInt(year), 0, 1 );
		periodEnd = (Calendar)atDate.clone();
		
		
		if (predFinish.compareTo(periodEnd.getTime())<0 ) {
			yearFactor = (float)(predFinish.getTime() - periodBegin.getTime().getTime())/(periodEnd.getTime().getTime()-periodBegin.getTime().getTime());
			periodEnd.clear();
			periodEnd.setTime( predFinish );			
		}

		logger.debug("MTh yearlyGU, begin "+periodBegin.getTime());
		logger.debug("MTh yearlyGU, end "+periodEnd.getTime());
		
		for ( Progress entry : this.getProgresses() ) {
			logger.debug("MTh yearlyGU, entry "+ entry.getDate());

			if ( entry.getDate().compareTo(periodEnd.getTime()) < 0 && entry.getDate().compareTo(periodBegin.getTime())>= 0  ) {
				slots.add(entry);
				logger.debug("MTh yearlyGU, entry fits");
			} else if ( (slots.isEmpty() || slots.get(slots.size()-1).getDate().after(periodBegin.getTime()) ) && (entry.getDate().compareTo(periodBegin.getTime()) < 0) ) {
				slots.add(entry);
				logger.debug("MTh yearlyGU, entry should be added as IB");
			}
		}
		
		if (logger.isDebugEnabled()) {
			for (Progress ent : slots) {
				logger.debug("MTh yearlyGU "+ ent.getDate());
			}
		}

		Progress next = null;
		float pGU = 0.0f;
		for (Progress slot : slots) {
			if (next!= null) {
				float span = next.getDate().getTime() - (slot.getDate().compareTo(periodBegin.getTime()) > 0 ? slot.getDate().getTime() : periodBegin.getTime().getTime());
				pGU+= span/(periodEnd.getTime().getTime()-periodBegin.getTime().getTime())*slot.getActivity()*slot.getGuFraction();
				logger.debug("MTh yearlyGU, diff "+ span/(24*60*60*1000) + ", "+pGU );
			} else {
				float span = periodEnd.getTime().getTime() - (slot.getDate().compareTo(periodBegin.getTime()) > 0 ? slot.getDate().getTime() : periodBegin.getTime().getTime());
				pGU+= span/(periodEnd.getTime().getTime()-periodBegin.getTime().getTime())*slot.getActivity()*slot.getGuFraction();
				logger.debug("MTh yearlyGU, (first) diff "+ span/(24*60*60*1000) + ", "+pGU );
			
			}
			next = slot;
		}
		
        return pGU * yearFactor;
    }

	public Float remainingProjectTime (Date atDate, boolean ignoreSameDateEntry)
	{

		logger.debug("MTh remainingProjectTime, date "+ atDate );

		float remain = 0.0f;
		
		if ( this.getStart().compareTo(atDate) != 0 ) {
			
			List<Progress> slots = new ArrayList<Progress>();
		
			for ( Progress entry : this.getProgresses() ) {
				logger.debug("MTh remainingProjectTime, entry "+ entry.getDate());
	
				if ( entry.getDate().compareTo(atDate) <= 0 ) {
					slots.add(entry);
					logger.debug("MTh remainingProjectTime, entry fits");
					if ( entry.getRemainingMonths() != null && !(ignoreSameDateEntry && entry.getDate().compareTo(atDate) == 0) ) {
						break;
					}
				}
			}
	
	//		long timeSpan =  atDate.getTime() - slots.get(slots.size()-1).getDate().getTime();
			
			Float remainingStart = slots.size() == 0 ? 
				REMAIN_AT_START : 
				(slots.get(slots.size()-1).getRemainingMonths() == null ? 
					REMAIN_AT_START : 
					slots.get(slots.size()-1).getRemainingMonths() )
			;
			
			Progress next = null;
			float elapsed = 0.0f;
			float extraTime = 0.0f;
			
			for (Progress slot : slots) {
				float span;
				if (next!= null) {
					span = next.getDate().getTime() - slot.getDate().getTime();
				} else {
					span = atDate.getTime() - slot.getDate().getTime();
				
				}
				elapsed+= span*slot.getActivity()*slot.getProjectFraction();
				extraTime+= slot.getAddedMonths() != null ? slot.getAddedMonths() : 0.0f;
				
				next = slot;
	
				logger.debug("MTh remainingProjectTime, diff "+ span/(24*60*60*1000) + ", "+elapsed/(24*60*60*1000) );
			}
	
			remain = remainingStart - elapsed/MONTH_IN_MILLS + extraTime;

			logger.debug("MTh remainingProjectTime is "+ remain );

		} else {

				remain = REMAIN_AT_START;
		}
		
		return remain;
	}
	
	public Float currentRemainingProjectTime()
	{
		return remainingProjectTime( Calendar.getInstance().getTime(), false );
	}


	/* Private utility methods */
	
	private Date predictDate (float months)
	{
	
		logger.debug("MTh predictedHalfTime " );

		
		if (this.progresses.size()>0) {
			Calendar now = Calendar.getInstance();
			String thisYear = Integer.toString((now.get(Calendar.YEAR)));

			Calendar ey = Calendar.getInstance();
			ey.set(Calendar.MONTH, Calendar.DECEMBER);
			ey.set(Calendar.DATE, 31);
	
			java.util.Iterator<Progress> entryIterator = this.getProgresses().iterator();
			Progress entry = entryIterator.next();
			while (entryIterator.hasNext() && months > remainingProjectTime(entry.getDate(), false) ) {
				entry = entryIterator.next();
			}
			float remainAtLatest = remainingProjectTime(entry.getDate(), false);

			float remainPredInDays = (remainAtLatest-months)*MONTH_IN_DAYS/(entry.getActivity()*entry.getProjectFraction()) ;
				
			Calendar endDate = Calendar.getInstance();
			endDate.setTime(entry.getDate());
			endDate.add(Calendar.DAY_OF_MONTH,Math.round(remainPredInDays));

			logger.debug("MTh predictedHalfTime is "+ endDate.getTime() );
		
			return endDate.getTime();	
		} else {
			return new Date();
		}
	}

 
	private Float endYearRemainingProjectTime()
	{
		Calendar ey = Calendar.getInstance();
		ey.set(Calendar.MONTH, Calendar.DECEMBER);
		ey.set(Calendar.DATE, 31);
		return remainingProjectTime( ey.getTime(), false );
	}

	private Float endPrevYearRemainTime()
	{
		Calendar ey = Calendar.getInstance();
		ey.set(Calendar.YEAR, ey.get(Calendar.YEAR) - 1);
		ey.set(Calendar.MONTH, Calendar.DECEMBER);
		ey.set(Calendar.DATE, 31);
		return remainingProjectTime( ey.getTime(), false );
	}


	/* Constructors */
	
	public PhDPosition() {	}			    
}
