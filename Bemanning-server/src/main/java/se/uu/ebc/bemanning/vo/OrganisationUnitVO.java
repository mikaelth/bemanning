package se.uu.ebc.bemanning.vo;

import se.uu.ebc.bemanning.entity.OrganisationUnit;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@Slf4j
public class OrganisationUnitVO {


    private Long id;
    private String svName;
    private String enName;
    private String unitKind;
    private String abbreviation;
    private Boolean inSystem;
    private Boolean legacyUnit;
    private Boolean courseEconomyHolder;




	public OrganisationUnitVO(OrganisationUnit ou) {
		this.id = ou.getId();
		this.svName = ou.getSvName();
		this.enName = ou.getEnName();
		this.unitKind = ou.getUnitKind();
		this.abbreviation = ou.getAbbreviation();
		this.inSystem = ou.getInSystem();
		this.legacyUnit = ou.getLegacyUnit();
		this.courseEconomyHolder = ou.getCourseEconomyHolder();
	}

}