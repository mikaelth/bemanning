package se.uu.ebc.bemanning.repo;

import jakarta.persistence.TypedQuery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import se.uu.ebc.bemanning.entity.PhDPosition;
import se.uu.ebc.bemanning.entity.Person;

import java.util.Set;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface PhDPositionRepo extends JpaRepository<PhDPosition, Long>, JpaSpecificationExecutor<PhDPosition>{

    // findById(Long id) is inherited from JpaRepository and returns Optional<PhDPosition>
    // findAll() is inherited from JpaRepository
	
	@Query("SELECT p FROM PhDPosition AS p WHERE p.person = ?1")
	PhDPosition findByPerson(Person p);
}
