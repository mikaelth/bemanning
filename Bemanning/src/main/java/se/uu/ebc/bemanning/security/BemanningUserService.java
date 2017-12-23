package se.uu.ebc.bemanning.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.Authentication;

public interface BemanningUserService extends AuthenticationUserDetailsService {
    
    public UserDetails loadUserByUsername(String username);
    public UserDetails loadUserDetails(Authentication token);

}
