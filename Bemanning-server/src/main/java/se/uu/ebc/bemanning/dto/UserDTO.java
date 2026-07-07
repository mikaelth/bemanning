package se.uu.ebc.bemanning.dto;

import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;


import se.uu.ebc.bemanning.enums.UserRoles;

import lombok.*;

import lombok.extern.slf4j.Slf4j;

@Data
//@NoArgsConstructor
@Builder
@Slf4j
public class UserDTO {
	private Long id;
    private String username;
  	private String formName;
	private String name;
	private Map<String, String> principalDepts;
	private Set<UserRoles> userRoles;

/* 
	private Map<String, String> principalDepts = new HashMap<String, String>();
	private Set<UserRoles> userRoles = new HashSet<UserRoles>();
 */
}
