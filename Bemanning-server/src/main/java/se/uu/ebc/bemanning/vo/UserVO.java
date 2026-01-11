package se.uu.ebc.bemanning.vo;

import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;


import se.uu.ebc.bemanning.enums.UserRoleType;

import lombok.*;

import lombok.extern.slf4j.Slf4j;

@Data
//@NoArgsConstructor
@Builder
@Slf4j
public class UserVO {
	private Long id;
    private String username;
  	private String formName;
	private String name;
	private Map<String, String> principalDepts = new HashMap<String, String>();
	private Set<UserRoleType> userRoles = new HashSet<UserRoleType>();

}
