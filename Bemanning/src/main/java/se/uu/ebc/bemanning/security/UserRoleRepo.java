package se.uu.ebc.bemanning.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import se.uu.ebc.bemanning.security.UserRole;

@Repository
public interface UserRoleRepo extends JpaRepository<UserRole, Long>, JpaSpecificationExecutor<UserRole>{
}
