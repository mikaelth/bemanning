package se.uu.ebc.bemanning.vo;

import com.poiji.annotation.ExcelCellName;
import com.poiji.annotation.ExcelRow;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class PrimulaEntriesExcel {

	private static final float MONTH_IN_HOURS = (1700.0f/12.0f);

    @ExcelRow
    private int rowIndex;

    @ExcelCellName("Personnr")
    private String pNIN;

    @ExcelCellName("   Omf")
    private Float omf;

    @ExcelCellName("  Komf")
    private Float kOmf;

    @ExcelCellName("      Lön+LBK")
    private Float cost;

	@Builder.Default
	private boolean updated =  false;
	
	
	public Float hourlyCost() {
		Double c = Math.ceil((cost/((omf*kOmf)/(100*100)))/MONTH_IN_HOURS);
		return c.floatValue();
	}
	
	public String pNINForLdap() {
		String[] parts = pNIN.split("-");
		
		if (Integer.parseInt(parts[0]) < 551231) {
			return "20"+parts[0]+parts[1];
		} else {
			return "19"+parts[0]+parts[1];		
		}
	}
}