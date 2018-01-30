package se.uu.ebc.ldap;

import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import se.uu.ebc.ldap.AKKAUser;
import javax.naming.Name;

@Repository
public interface AKKAUserRepo extends LdapRepository<AKKAUser> {
//public interface AKKAUserRepo extends CrudRepository<AKKAUser, Name> {

	public AKKAUser findByUsername(String username);

}
