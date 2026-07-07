package  se.uu.ebc.bemanning.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@Slf4j
public class CourseDTO {

    private Long id;

    private String code;
    
    private String seName;
    
    private String enName;
    
    private String courseGroup;
    
    private String note;
    
    private Float credits;
    
/* 
    private Integer effortAdmin;
    
    private Integer effortDevelopment;
    
    private Integer effortLecture;
    
    private Integer effortPractical;
    
    private Integer effortExcursion;
    
    private Integer effortSeminar;
 */
    
    
    
}
