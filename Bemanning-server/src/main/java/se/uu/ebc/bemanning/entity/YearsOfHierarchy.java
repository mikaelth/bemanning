package se.uu.ebc.bemanning.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "YEARS_OF_HIERARCHY")
@Getter
@Setter
//@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode(callSuper = true)
@Slf4j
public class YearsOfHierarchy  extends Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    
    @Column(name = "FIRST_YEAR")
    private Integer firstYear;
    
    @Column(name = "LAST_YEAR")
    private Integer lastYear;
    
    @ManyToOne
    @NotNull
    @JoinColumn(name = "SUPER_UNIT_FK")
	private OrganisationUnit superUnit;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "SUB_UNIT_FK")
	private OrganisationUnit subUnit;

    @Column(name = "NOTE")
    private String note;


    
    /* Public methods */
    
    public boolean valid(String year)
    {
		boolean valid = false;
		try {
			Integer current = Integer.parseInt(year);
			valid = valid(current);
		} catch (NumberFormatException nx) {
			log.error("MTh YearsOfHierarchy.valid(String year) experienced pesky exception "+ nx);			
		}
		return valid;
	}

    public boolean valid(Integer year)
    {
		return ( this.getFirstYear()<=year && (this.getLastYear()==null || this.getLastYear()>=year) );
	}
    
    
}
