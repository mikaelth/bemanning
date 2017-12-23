package se.uu.ebc.bemanning.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BemanningUser
    extends User
{

	private static final long serialVersionUID = 1210242130L;
	
	private Map<String,String> deptMap = new HashMap<String,String> ();
    private Log logger = LogFactory.getLog(BemanningUser.class);
	
	private BemanningUser()
	{
		super("dummy", "token", false, false, false, false, new HashSet<GrantedAuthority>());
	}
	
	public BemanningUser(
		java.lang.String username, 
		java.lang.String password, 
		boolean enabled, 
		boolean accountNonExpired, 
		boolean credentialsNonExpired, 
		boolean accountNonLocked, 
		Collection<GrantedAuthority> authorities)
	{
		super(username, password, enabled, accountNonExpired, credentialsNonExpired,accountNonLocked, authorities);
	}

    public  void putDept(String year, String dept)
    {
		deptMap.put(year,dept);
    }

    public  String getDept(String year)
    {
		return deptMap.get(year);
    }

	public Map<String,String> getAllDepts()
	{
		return this.deptMap;
	}
}