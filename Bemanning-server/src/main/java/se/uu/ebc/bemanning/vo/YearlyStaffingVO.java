package  se.uu.ebc.bemanning.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se.uu.ebc.bemanning.entity.FactorRecord;

@Data
@NoArgsConstructor
@Slf4j
public class YearlyStaffingVO {

    
    private Long id;
    private String dept;
    private String year;
	private FactorRecord teachingFactors;
//    private Map<String, Float> teachingFactors;
}
