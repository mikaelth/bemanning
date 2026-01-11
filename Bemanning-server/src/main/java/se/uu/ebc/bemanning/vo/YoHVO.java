package se.uu.ebc.bemanning.vo;

import se.uu.ebc.bemanning.entity.YearsOfHierarchy;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@Slf4j
public class YoHVO {

    private Long id;
    private Integer firstYear;
    private Integer lastYear;

	private Long superUnitId;
	private Long unitId;

	private String note;

	





	public YoHVO(YearsOfHierarchy xe){
		this.id = xe.getId();
		this.firstYear = xe.getFirstYear();
		this.lastYear = xe.getLastYear();
		this.superUnitId = xe.getSuperUnit().getId();
		this.unitId = xe.getSubUnit().getId();
		this.note = xe.getNote();	
	}
}