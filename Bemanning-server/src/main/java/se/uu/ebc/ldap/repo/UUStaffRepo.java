package se.uu.ebc.ldap.repo;

import jakarta.persistence.TypedQuery;

import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import se.uu.ebc.ldap.entity.UUStaff;
import se.uu.ebc.bemanning.enums.EmploymentType;

import java.util.Set;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface UUStaffRepo extends LdapRepository<UUStaff> {

  @Query("(&(employmentType=*)(!(employmentType=Hired))(mail=:mail))")
  UUStaff findEmployeeByMail(String mail);


}

