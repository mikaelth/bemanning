package  se.uu.ebc.bemanning.entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Collections;
import java.util.Comparator;

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

import org.apache.log4j.Logger;

import org.springframework.format.annotation.DateTimeFormat;

import se.uu.ebc.bemanning.entity.PhDPosition;
import se.uu.ebc.bemanning.entity.Progress;

@Entity
@Table(name = "PH_D_POSITION")
public class PhDPosition {

    @Transient
    private static Logger logger = Logger.getLogger(PhDPosition.class.getName());

	private static final float MONTH_IN_MILLS = 24*60*60*1000*(365.0f/12.0f);
	private static final float HOUR_IN_MILLS = 60*60*1000.0f;
	private static final float MONTH_IN_DAYS = (365.0f/12.0f);
	private static final float REMAIN_AT_START = 48.0f;
	private static final float REMAIN_AT_HALF = 24.0f;
	private static final float REMAIN_AT_80 = 0.2f*48.0f;
    
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

	public Person getPerson()
	{
		return this.person;
	}

	public void setPerson(Person person)
	{
		this.person = person;
	}

    
	@OrderBy("date ASC")
    @OneToMany(mappedBy = "phdPosition")
    private List<Progress> progresses;
    
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
	
	// Hack!!!
//    public Float yearlyGU(String year) { return 0.2f; }

/* 
	public Date predictedFinishDate()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("MTh predictedFinishDate " );
		}
		
		Calendar now = Calendar.getInstance();
		String thisYear = Integer.toString((now.get(Calendar.YEAR)));

		Calendar ey = Calendar.getInstance();
		ey.set(Calendar.MONTH, Calendar.DECEMBER);
		ey.set(Calendar.DATE, 31);
	
		Progress latestEntry = this.getProgresses().iterator().next();
		float remainAtLatest = remainingProjectTime(latestEntry.getDate(), false);

		float remainPredInDays = remainAtLatest*MONTH_IN_DAYS/(latestEntry.getActivity()*latestEntry.getProjectFraction()) ;
				
		Calendar endDate = latestEntry.getDate();
		endDate.add(Calendar.DAY_OF_MONTH,Math.round(remainPredInDays));

		if (logger.isDebugEnabled()) {
			logger.debug("MTh predictedFinishDate is "+ endDate.getTime() );
		}
		
		return endDate.getTime();
	}
	
	public Date predictedHalfTime()
	{
		return predictDate(REMAIN_AT_HALF);
	}

 	public Date predicted80Percent()
	{
		return predictDate(REMAIN_AT_80);
	}

    public Float yearlyGU(String year)
    {
		if (logger.isDebugEnabled()) {
			logger.debug("MTh yearlyGU "+year );
		}
		
		float yearFactor = 1.0f;
		Date predFinish = this.predictedFinishDate();
		
		Calendar periodBegin = Calendar.getInstance();
		Calendar periodEnd = Calendar.getInstance();
		List<Progress> slots = new ArrayList<Progress>();
		
		periodBegin.clear();
		periodEnd.clear();
		periodBegin.set( Integer.parseInt(year), 0, 1 );
		periodEnd.set( Integer.parseInt(year)+1, 0, 1 );
		
		
		if (predFinish.compareTo(periodEnd.getTime())<0 ) {
			yearFactor = (float)(predFinish.getTime() - periodBegin.getTime().getTime())/(periodEnd.getTime().getTime()-periodBegin.getTime().getTime());
			periodEnd.clear();
			periodEnd.setTime( predFinish );			
		}

		if (logger.isDebugEnabled()) {
			logger.debug("MTh yearlyGU, begin "+periodBegin.getTime());
			logger.debug("MTh yearlyGU, end "+periodEnd.getTime());
		}
		
		for ( Progress entry : this.getProgresses() ) {
			if (logger.isDebugEnabled()) {
				logger.debug("MTh yearlyGU, entry "+ entry.getDate());
			}

			if ( entry.getDate().compareTo(periodEnd) < 0 && entry.getDate().compareTo(periodBegin)>= 0  ) {
				slots.add(entry);
				if (logger.isDebugEnabled()) {
					logger.debug("MTh yearlyGU, entry fits");
				}
			} else if ( (slots.isEmpty() || slots.get(slots.size()-1).getDate().after(periodBegin) ) && (entry.getDate().compareTo(periodBegin) < 0) ) {
				slots.add(entry);
				if (logger.isDebugEnabled()) {
					logger.debug("MTh yearlyGU, entry should be added as IB");
				}
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
				float span = next.getDate().getTimeInMillis() - (slot.getDate().compareTo(periodBegin) > 0 ? slot.getDate().getTimeInMillis() : periodBegin.getTimeInMillis());
				pGU+= span/(periodEnd.getTime().getTime()-periodBegin.getTime().getTime())*slot.getActivity()*slot.getGuFraction();
				if (logger.isDebugEnabled()) {
					logger.debug("MTh yearlyGU, diff "+ span/(24*60*60*1000) + ", "+pGU );
				}
			} else {
				float span = periodEnd.getTimeInMillis() - (slot.getDate().compareTo(periodBegin) > 0 ? slot.getDate().getTimeInMillis() : periodBegin.getTimeInMillis());
				pGU+= span/(periodEnd.getTimeInMillis()-periodBegin.getTimeInMillis())*slot.getActivity()*slot.getGuFraction();
				if (logger.isDebugEnabled()) {
					logger.debug("MTh yearlyGU, (first) diff "+ span/(24*60*60*1000) + ", "+pGU );
				}
			
			}
			next = slot;
		}
		
        return pGU * yearFactor;
    }

	public Float remainingProjectTime (Calendar atDate, boolean ignoreSameDateEntry)
	{

		if (logger.isDebugEnabled()) {
			logger.debug("MTh remainingProjectTime, date "+ atDate );
		}

		float remain = 0.0f;
		
		if ( this.getStart().compareTo(atDate) != 0 ) {
			
			List<Progress> slots = new ArrayList<Progress>();
		
			for ( Progress entry : this.getProgresses() ) {
				if (logger.isDebugEnabled()) {
					logger.debug("MTh remainingProjectTime, entry "+ entry.getDate());
				}
	
				if ( entry.getDate().compareTo(atDate) <= 0 ) {
					slots.add(entry);
					if (logger.isDebugEnabled()) {
						logger.debug("MTh remainingProjectTime, entry fits");
					}
					if ( entry.getRemainingMonths() != null && !(ignoreSameDateEntry && entry.getDate().compareTo(atDate) == 0) ) {
						break;
					}
				}
			}
	
	//		long timeSpan =  atDate.getTime() - slots.get(slots.size()-1).getDate().getTime();
			
			Float remainingStart = slots.size() == 0 ? REMAIN_AT_START : slots.get(slots.size()-1).getRemainingMonths();
			
			Progress next = null;
			float elapsed = 0.0f;
			float extraTime = 0.0f;
			
			for (Progress slot : slots) {
				float span;
				if (next!= null) {
					span = next.getDate().getTimeInMillis() - slot.getDate().getTimeInMillis();
				} else {
					span = atDate.getTimeInMillis() - slot.getDate().getTimeInMillis();
				
				}
				elapsed+= span*slot.getActivity()*slot.getProjectFraction();
				extraTime+= slot.getAddedMonths() != null ? slot.getAddedMonths() : 0.0f;
				
				next = slot;
	
				if (logger.isDebugEnabled()) {
					logger.debug("MTh remainingProjectTime, diff "+ span/(24*60*60*1000) + ", "+elapsed/(24*60*60*1000) );
				}
			}
	
			remain = remainingStart - elapsed/MONTH_IN_MILLS + extraTime;

			if (logger.isDebugEnabled()) {
				logger.debug("MTh remainingProjectTime is "+ remain );
				} 

		} else {

				remain = REMAIN_AT_START;
		}
		
		return remain;
	}
	
	public Float currentRemainingProjectTime()
	{
		return remainingProjectTime( Calendar.getInstance(), false );
	}

	private Date predictDate (float months)
	{
	
		if (logger.isDebugEnabled()) {
			logger.debug("MTh predictedHalfTime " );
		}
		
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
		endDate = entry.getDate();
		endDate.add(Calendar.DAY_OF_MONTH,Math.round(remainPredInDays));

		if (logger.isDebugEnabled()) {
			logger.debug("MTh predictedHalfTime is "+ endDate.getTime() );
		}
		
		return endDate.getTime();	
	
	}

	private Float endYearRemainingProjectTime()
	{
		Calendar ey = Calendar.getInstance();
		ey.set(Calendar.MONTH, Calendar.DECEMBER);
		ey.set(Calendar.DATE, 31);
		return remainingProjectTime( ey, false );
	}
	
 */

	public Date predictedFinishDate()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("MTh predictedFinishDate " );
		}
		
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

		if (logger.isDebugEnabled()) {
			logger.debug("MTh predictedFinishDate is "+ endDate.getTime() );
		}
		
		return endDate.getTime();
	}
	
 	public Date predictedHalfTime()
	{
		return predictDate(REMAIN_AT_HALF);
	}

	public Date predicted80Percent()
	{
		return predictDate(REMAIN_AT_80);
	}

    public Float yearlyGU(String year)
    {
		if (logger.isDebugEnabled()) {
			logger.debug("MTh yearlyGU "+year );
		}
		
		float yearFactor = 1.0f;
		Date predFinish = this.predictedFinishDate();
		
		Calendar periodBegin = Calendar.getInstance();
		Calendar periodEnd = Calendar.getInstance();
		List<Progress> slots = new ArrayList<Progress>();
		
		periodBegin.clear();
		periodEnd.clear();
		periodBegin.set( Integer.parseInt(year), 0, 1 );
		periodEnd.set( Integer.parseInt(year)+1, 0, 1 );
		
		
		if (predFinish.compareTo(periodEnd.getTime())<0 ) {
			yearFactor = (float)(predFinish.getTime() - periodBegin.getTime().getTime())/(periodEnd.getTime().getTime()-periodBegin.getTime().getTime());
			periodEnd.clear();
			periodEnd.setTime( predFinish );			
		}

		if (logger.isDebugEnabled()) {
			logger.debug("MTh yearlyGU, begin "+periodBegin.getTime());
			logger.debug("MTh yearlyGU, end "+periodEnd.getTime());
		}
		
		for ( Progress entry : this.getProgresses() ) {
			if (logger.isDebugEnabled()) {
				logger.debug("MTh yearlyGU, entry "+ entry.getDate());
			}

			if ( entry.getDate().compareTo(periodEnd.getTime()) < 0 && entry.getDate().compareTo(periodBegin.getTime())>= 0  ) {
				slots.add(entry);
				if (logger.isDebugEnabled()) {
					logger.debug("MTh yearlyGU, entry fits");
				}
			} else if ( (slots.isEmpty() || slots.get(slots.size()-1).getDate().after(periodBegin.getTime()) ) && (entry.getDate().compareTo(periodBegin.getTime()) < 0) ) {
				slots.add(entry);
				if (logger.isDebugEnabled()) {
					logger.debug("MTh yearlyGU, entry should be added as IB");
				}
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
				if (logger.isDebugEnabled()) {
					logger.debug("MTh yearlyGU, diff "+ span/(24*60*60*1000) + ", "+pGU );
				}
			} else {
				float span = periodEnd.getTime().getTime() - (slot.getDate().compareTo(periodBegin.getTime()) > 0 ? slot.getDate().getTime() : periodBegin.getTime().getTime());
				pGU+= span/(periodEnd.getTime().getTime()-periodBegin.getTime().getTime())*slot.getActivity()*slot.getGuFraction();
				if (logger.isDebugEnabled()) {
					logger.debug("MTh yearlyGU, (first) diff "+ span/(24*60*60*1000) + ", "+pGU );
				}
			
			}
			next = slot;
		}
		
        return pGU * yearFactor;
    }

	public Float remainingProjectTime (Date atDate, boolean ignoreSameDateEntry)
	{

		if (logger.isDebugEnabled()) {
			logger.debug("MTh remainingProjectTime, date "+ atDate );
		}

		float remain = 0.0f;
		
		if ( this.getStart().compareTo(atDate) != 0 ) {
			
			List<Progress> slots = new ArrayList<Progress>();
		
			for ( Progress entry : this.getProgresses() ) {
				if (logger.isDebugEnabled()) {
					logger.debug("MTh remainingProjectTime, entry "+ entry.getDate());
				}
	
				if ( entry.getDate().compareTo(atDate) <= 0 ) {
					slots.add(entry);
					if (logger.isDebugEnabled()) {
						logger.debug("MTh remainingProjectTime, entry fits");
					}
					if ( entry.getRemainingMonths() != null && !(ignoreSameDateEntry && entry.getDate().compareTo(atDate) == 0) ) {
						break;
					}
				}
			}
	
	//		long timeSpan =  atDate.getTime() - slots.get(slots.size()-1).getDate().getTime();
			
			Float remainingStart = slots.size() == 0 ? REMAIN_AT_START : slots.get(slots.size()-1).getRemainingMonths();
			
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
	
				if (logger.isDebugEnabled()) {
					logger.debug("MTh remainingProjectTime, diff "+ span/(24*60*60*1000) + ", "+elapsed/(24*60*60*1000) );
				}
			}
	
			remain = remainingStart - elapsed/MONTH_IN_MILLS + extraTime;

			if (logger.isDebugEnabled()) {
				logger.debug("MTh remainingProjectTime is "+ remain );
				} 

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
	
		if (logger.isDebugEnabled()) {
			logger.debug("MTh predictedHalfTime " );
		}
		
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

		if (logger.isDebugEnabled()) {
			logger.debug("MTh predictedHalfTime is "+ endDate.getTime() );
		}
		
		return endDate.getTime();	
	
	}

 
	private Float endYearRemainingProjectTime()
	{
		Calendar ey = Calendar.getInstance();
		ey.set(Calendar.MONTH, Calendar.DECEMBER);
		ey.set(Calendar.DATE, 31);
		return remainingProjectTime( ey.getTime(), false );
	}

			    
}
