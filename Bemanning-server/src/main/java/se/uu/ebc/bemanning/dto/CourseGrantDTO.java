package  se.uu.ebc.bemanning.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import se.uu.ebc.bemanning.enums.GrantType;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@Slf4j
public class CourseGrantDTO {

    private Long id;

	private Long courseInstanceId;
	private Long debitUnitId;
	private Long departmentId;

    private Integer amount;
    private GrantType type;
    private String note;

	@JsonInclude(Include.NON_NULL)
    private LocalDateTime setDate;


}