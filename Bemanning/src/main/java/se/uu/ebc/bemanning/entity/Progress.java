package  se.uu.ebc.bemanning.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import  se.uu.ebc.bemanning.entity.Progress;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PROGRESS")
public class Progress {
    
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

    @ManyToOne
    @NotNull
    @JoinColumn(name = "PH_D_POSITION_FK")
	private PhDPosition phdPosition;

	public PhDPosition getPhdPosition()
	{
		return this.phdPosition;
	}

	public void setPhdPosition(PhDPosition phdPosition)
	{
		this.phdPosition = phdPosition;
	}

	    
    @Column(name = "DATE")
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "MM")
    private Date date;
    
    @Column(name = "ACTIVITY", precision = 12)
    private Float activity;
    
    @Column(name = "PROJECT_FRACTION", precision = 12)
    private Float projectFraction;
    
    @Column(name = "GU_FRACTION", precision = 12)
    private Float guFraction;
    
    @Column(name = "TO_ECO_SYS")
    @NotNull
    private Short toEcoSys;
    
    @Column(name = "REMAINING_MONTHS", precision = 12)
    private Float remainingMonths;
    
    @Column(name = "TO_UP_DOK")
    @NotNull
    private Short toUpDok;
    
    @Column(name = "NOTE", length = 255)
    private String note;
    
    @Column(name = "ADDED_MONTHS", precision = 12)
    private Float addedMonths;
    
    public Date getDate() {
        return date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    public Float getActivity() {
        return activity;
    }
    
    public void setActivity(Float activity) {
        this.activity = activity;
    }
    
    public Float getProjectFraction() {
        return projectFraction;
    }
    
    public void setProjectFraction(Float projectFraction) {
        this.projectFraction = projectFraction;
    }
    
    public Float getGuFraction() {
        return guFraction;
    }
    
    public void setGuFraction(Float guFraction) {
        this.guFraction = guFraction;
    }
    
    public Short getToEcoSys() {
        return toEcoSys;
    }
    
    public void setToEcoSys(Short toEcoSys) {
        this.toEcoSys = toEcoSys;
    }
    
    public Float getRemainingMonths() {
        return remainingMonths;
    }
    
    public void setRemainingMonths(Float remainingMonths) {
        this.remainingMonths = remainingMonths;
    }
    
    public Short getToUpDok() {
        return toUpDok;
    }
    
    public void setToUpDok(Short toUpDok) {
        this.toUpDok = toUpDok;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    public Float getAddedMonths() {
        return addedMonths;
    }
    
    public void setAddedMonths(Float addedMonths) {
        this.addedMonths = addedMonths;
    }
    
    
    public int compareTo(Progress o)
    {
        return -(this.getDate().compareTo( o.getDate() ));
    }

	public Float calcRemainMonths()
	{
		return this.getPhdPosition().remainingProjectTime(this.getDate(), true );
	}
    
}
