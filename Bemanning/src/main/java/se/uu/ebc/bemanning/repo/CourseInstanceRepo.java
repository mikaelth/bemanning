package se.uu.ebc.bemanning.repo;

import javax.persistence.TypedQuery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import se.uu.ebc.bemanning.entity.Person;
import se.uu.ebc.bemanning.entity.Staff;
import se.uu.ebc.bemanning.entity.OrganisationUnit;
import se.uu.ebc.bemanning.entity.CourseInstance;

import java.util.Set;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface CourseInstanceRepo extends JpaRepository<CourseInstance, Long>, JpaSpecificationExecutor<CourseInstance>{

    public CourseInstance findById(Long id);  

	@Query("SELECT ci FROM CourseInstance AS ci WHERE ci.year = ?1")
	public List<CourseInstance> findByYear(String year);    
 
	@Query("SELECT ci FROM CourseInstance AS ci WHERE ci.year = ?1 AND ci.courseLeader = ?2 order by ci.course.courseGroup, ci.course.code")
	public List<CourseInstance> findByYearAndCourseLeader( String year, Staff staff);


	@Query(value = "(select ci.*, c.course_group as cgroup, c.se_name as cname from course_instance as ci join course as c on c.id=ci.course_fk join assignment as a on a.COURSE_INSTANCE_FK=ci.id join organisation_unit as ou on ou.id=a.department_fk where (ci.year=:year and ou.abbreviation = :dept)) union distinct "+
			"(select ci.*, c.course_group, c.se_name from course_instance as ci join course as c on c.id=ci.course_fk join assignment as a on a.COURSE_INSTANCE_FK=ci.id join staff as s on s.id=a.staff_fk join organisation_unit as ou on ou.id=s.ou_fk where (ci.year=:year and ou.abbreviation IN (:ous))) union distinct "+
			"(select ci.*, c.course_group, c.se_name from course_instance as ci join course as c on c.id=ci.course_fk join staff as cl on cl.id=ci.course_leader_fk join organisation_unit as ou on ou.id=cl.ou_fk where (ci.year=:year and ou.abbreviation IN (:ous))) union distinct "+
			"(select ci.*, c.course_group, c.se_name from course_instance as ci join course as c on c.id=ci.course_fk join course_grant as g on ci.id=g.course_instance_fk join organisation_unit as ou on ou.id=g.department_fk where (ci.year=:year and ou.abbreviation = :dept)) "+
			"order by cgroup,cname;", nativeQuery = true 
		)
    public List<CourseInstance> loadInvolvedCourses(@Param("ous") String[] ous, @Param("dept") String dept, @Param("year") String year);
 

}
