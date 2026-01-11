package se.uu.ebc.bemanning.repo;

import jakarta.persistence.TypedQuery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import se.uu.ebc.bemanning.entity.Assignment;

import java.util.Set;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface AssignmentRepo extends JpaRepository<Assignment, Long>, JpaSpecificationExecutor<Assignment>{

    // findById(Long id) is inherited from JpaRepository and returns Optional<Assignment>
}
