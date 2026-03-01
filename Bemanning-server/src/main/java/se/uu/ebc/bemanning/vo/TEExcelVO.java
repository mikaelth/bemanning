package se.uu.ebc.bemanning.vo;

import java.util.List;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import se.uu.ebc.bemanning.enums.ActivityType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class TEExcelVO {

	private String activity;
	private ActivityType activityType;
	private String staff;
	private Float actTime;
	private Float duration;
	private String courseCode;
	private String ciNumber;
	private boolean updated;


	public String givenName() {
		return staff.split(" ")[0];
	}
	
	public String familyName() {
		return staff.split(" ").length > 1 ? staff.split(" ")[1] : staff.split(" ")[0];
	}
}

