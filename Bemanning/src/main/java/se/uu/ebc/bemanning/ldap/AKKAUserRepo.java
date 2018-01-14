package se.uu.ebc.bemanning.ldap;

import se.uu.ebc.bemanning.entity.AKKAUser;
import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AKKAUserRepo extends LdapRepository<AKKAUser> {
    AKKAUser findByUsername(String username);
}
