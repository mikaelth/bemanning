package se.uu.ebc.bemanning.security;

import jakarta.persistence.TypedQuery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import se.uu.ebc.bemanning.entity.Person;

@Repository
@Transactional(readOnly = true)
public interface UserRepo extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person>{

	@Query("SELECT u FROM Person AS u WHERE u.username = ?1")
	public Person findUserByUsername(String username);

    // findById(Long id) is inherited from JpaRepository and returns Optional<Person>   

}
