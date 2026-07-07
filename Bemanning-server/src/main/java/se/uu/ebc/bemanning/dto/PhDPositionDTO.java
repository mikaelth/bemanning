package se.uu.ebc.bemanning.dto;

import java.time.LocalDateTime;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@Slf4j
public class PhDPositionDTO {



    private Long id;

	private Long personId;

 
    private String start;
//    private LocalDateTime start;
//    private LocalDateTime dissertation;

    private String note;
    private boolean inactive;

/* 
	private LocalDateTime predictedFinishDate;
 	private LocalDateTime predictedHalfTime;
	private LocalDateTime predicted80Percent;
	private Float currentRemainingProjectTime;
 */

	private String program;
	

}