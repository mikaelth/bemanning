package se.uu.ebc.bemanning.ldap.repository;

import jakarta.persistence.TypedQuery;

import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;
import org.springframework.ldap.core.LdapTemplate;

import se.uu.ebc.bemanning.entity.Person;
import se.uu.ebc.bemanning.enums.EmploymentType;
import se.uu.ebc.bemanning.ldap.model.StaffAkka;

import java.util.Set;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.util.List;

@Repository
@Transactional(readOnly = true)
//public interface StaffAkkaRepository extends LdapRepository<StaffAkka> {
public class StaffAkkaRepository {

    private final LdapTemplate ldapTemplate;

    public StaffAkkaRepository(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

/* This is for when the repo is an interface */
/*
  @Query("(&(employmentType=*)(!(employmentType=Hired))(mail=:mail))")
  StaffAkka findEmployeeByMail(String mail);
*/

    public List<StaffAkka> findByLastName(String lastName) {
        return ldapTemplate.find(
            query().where("objectclass").is("person").and("sn").is(lastName),
            StaffAkka.class
        );
    }

    public List<StaffAkka> findByPNIN(String pNIN) {
        return ldapTemplate.find(
            query().where("objectclass").is("person").and("norEduPersonNIN").is(pNIN),
            StaffAkka.class
        );
    }

    public List<StaffAkka> findByDepartment(String dept) {
        return ldapTemplate.find(
            query().where("objectclass").is("person").and("department").like(dept),
            StaffAkka.class
        );
    }

    public List<StaffAkka> findByProgramme(String prog) {
        return ldapTemplate.find(
            query().where("objectclass").is("person").and("ou").like(prog),
            StaffAkka.class
        );
    }

}

