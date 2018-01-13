package se.uu.ebc.bemanning.security;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.ArrayList;

import se.uu.ebc.bemanning.entity.Person;
import se.uu.ebc.bemanning.enums.UserRoleType;
import se.uu.ebc.bemanning.security.UserRepo;
import se.uu.ebc.bemanning.security.SecurityServiceException;
import se.uu.ebc.bemanning.security.BemanningUserService;

import org.apache.log4j.Logger;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.apache.log4j.Logger;


@Service ("securityService")
@Transactional(readOnly = true)
public class SecurityService implements BemanningUserService {


	private static Logger logger = Logger.getLogger(SecurityService.class.getName());

    @Autowired
    UserRepo userRepo;


	private final boolean ENABLED = true;
	private final boolean ACCOUNT_NON_EXPIRED = true;
	private final boolean CREDENTIALS_NON_EXPIRED = true;
	private final boolean ACCOUNT_NON_LOCKED = true;
	private final String ROLE_PREFIX = "ROLE_";
	
	
    /**
     * @see se.uu.ebc.bemanning.security.SecurityService#loadUserByUsername(String)
     */
    public UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException
    {
		if (logger.isDebugEnabled()) {
			logger.debug("MTh handleLoadUserByUsername, processing "+username);
		}
		logger.debug("loadUserByUsername, username: "+ username);

		if (username == null || username.trim().length() == 0) {
            throw new IllegalArgumentException(
                "se.uu.ebc.portaxnotes.security.loadUserByUsername(String username) - 'username' can not be null or empty");
        }
        try
        {
			logger.debug("loadUserByUsername, repo: "+ userRepo);

			Person localUser = userRepo.findUserByUsername(username);

			logger.debug("loadUserByUsername, localUser: "+ localUser);
	
			logger.debug("MTh handleLoadUserByUsername, got user "+localUser);
			logger.debug("MTh handleLoadUserByUsername, user is "+ReflectionToStringBuilder.toString(localUser, ToStringStyle.MULTI_LINE_STYLE));

			if (localUser == null) {
					throw new UsernameNotFoundException("User "+username+" not found!");
			}

			List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
 
/* 
			for (UserRole userRole : localUser.getUserRoles()) {
				authorities.add( new SimpleGrantedAuthority(ROLE_PREFIX + userRole.getRole().toString().toUpperCase()) );
				if (logger.isDebugEnabled()) {
					logger.debug("MTh loadUserByUsername, role "+userRole);
				}
			}
 */
 
			for (UserRoleType userRole : localUser.getUserRoles()) {
				authorities.add( new SimpleGrantedAuthority(ROLE_PREFIX + userRole.toString().toUpperCase()) );
				logger.debug("MTh loadUserByUsername, role "+userRole);
			}
 
//			authorities.add( new SimpleGrantedAuthority("ROLE_USER") );
			
			return new User(localUser.getUsername(), "token", ENABLED, ACCOUNT_NON_EXPIRED, CREDENTIALS_NON_EXPIRED, ACCOUNT_NON_LOCKED, authorities);
		} catch (Throwable th)
        {
            throw new SecurityServiceException(
                "Error performing 'SecurityService.loadUserByUsername(String username)' --> " + th,
                th);
        }

    }

    @Override
    public UserDetails loadUserDetails(Authentication token) 
    	throws UsernameNotFoundException 
    {
		logger.debug("MTh loadUserDetails, got token "+token);
		logger.debug("MTh loadUserDetails, got principal "+(String)token.getPrincipal());

		String username = token.getPrincipal() instanceof String ? (String)token.getPrincipal() : "";
        return loadUserByUsername(username);
    }
}
