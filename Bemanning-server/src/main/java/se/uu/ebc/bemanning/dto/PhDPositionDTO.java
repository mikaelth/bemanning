package se.uu.ebc.bemanning.dto;

import java.time.LocalDate;
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

 
    private LocalDate start;
    private LocalDate dissertation;

    private String note;
    private boolean inactive;

 
	private LocalDate predictedFinishDate;
 	private LocalDate predictedHalfTime;
	private LocalDate predicted80Percent;
	private Float currentRemainingProjectTime;
 

	private String program;
	

}