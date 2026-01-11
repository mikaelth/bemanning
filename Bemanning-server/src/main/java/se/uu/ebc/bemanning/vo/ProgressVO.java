package se.uu.ebc.bemanning.vo;

import java.time.LocalDateTime;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@Slf4j
public class ProgressVO {

    private Long id;

	private Long phdPositionId;
    private LocalDateTime date;

    private Float activity;
    private Float projectFraction;
    private Float guFraction;
    private boolean toEcoSys;
    private Float remainingMonths;
    private boolean toUpDok;
    private String note;
    private Float addedMonths;

}