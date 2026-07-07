package se.uu.ebc.bemanning.dto;

import java.util.Set;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;

import se.uu.ebc.bemanning.enums.UserRoles;

import lombok.*;

import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@Slf4j
public class PersonDTO {

    private Long id;
    private Set<UserRoles> userRoles;
   
    @NotBlank(message = "Given name is mandatory")
    private String givenName;
   
    @NotBlank(message = "Family name is mandatory")
    private String familyName;
   
    private boolean familyFirst;

    private String note;    

    @NotBlank(message = "username is mandatory")
    private String username;

    private boolean isActive;
	private String formName;
	private String name;
	
	private LocalDateTime creationDate;
	private LocalDateTime lastModifiedDate;

}
