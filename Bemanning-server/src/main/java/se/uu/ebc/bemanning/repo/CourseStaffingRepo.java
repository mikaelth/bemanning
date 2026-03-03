package se.uu.ebc.bemanning.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import se.uu.ebc.bemanning.entity.assignment.CourseStaffing;

@Repository
@Transactional(readOnly = true)
public interface CourseStaffingRepo extends JpaRepository<CourseStaffing, Long>, JpaSpecificationExecutor<CourseStaffing>{


 	@Query("SELECT cs FROM CourseStaffing AS cs WHERE (cs.courseInstance.instanceCode=?2 AND cs.courseInstance.course.code=?1 AND cs.courseInstance.year=?3 AND cs.staff.person.givenName=?4 and cs.staff.person.familyName=?5)")
 	Optional<CourseStaffing> findByCourseIntanceAndPerson(String courseCode, String instanceCode, String year, String givenName, String familyName);
    // findById(Long id) is inherited from JpaRepository and returns Optional<CourseStaffing>
}
