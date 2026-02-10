package se.uu.ebc.bemanning.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import se.uu.ebc.bemanning.entity.assignment.CourseStaffing;

@Repository
@Transactional(readOnly = true)
public interface CourseStaffingRepo extends JpaRepository<CourseStaffing, Long>, JpaSpecificationExecutor<CourseStaffing>{

    // findById(Long id) is inherited from JpaRepository and returns Optional<CourseStaffing>
}
