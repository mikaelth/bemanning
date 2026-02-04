package se.uu.ebc.bemanning.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;

public interface BemanningUserService extends AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {
    
    public UserDetails loadUserByUsername(String username);
    public UserDetails loadUserDetails(CasAssertionAuthenticationToken token);

}
