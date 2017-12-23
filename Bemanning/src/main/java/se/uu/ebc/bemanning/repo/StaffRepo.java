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

import java.util.Set;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface StaffRepo extends JpaRepository<Staff, Long>, JpaSpecificationExecutor<Staff>{

	@Query("SELECT s FROM Staff AS s WHERE s.person = ?1")
	public Set<Staff> findUserByPerson(Person person);

    public Staff findById(Long id);  
    
	@Query("SELECT s FROM Staff AS s WHERE s.year = ?1")
    public Set<Staff> findByYear(String Year);

	@Query("SELECT s FROM Staff AS s WHERE s.person = ?1 and s.year = ?2")
	public List<Staff> findUserByPersonAndYear(Person person, String year);

	@Query("SELECT distinct s.year as yr FROM Staff AS s order by yr DESC")
	public List<String> getStaffedYears();

	@Query("SELECT s FROM Staff AS s WHERE s.year = ?2 AND s.organisationUnit in ?1 order by s.person.familyName")
	public List<Staff> findUserByOuListAndYear(List<OrganisationUnit> ous, String year);

/* 
	@Query("SELECT s FROM Staff AS s where " +  
		"s.ou_fk = :#{#dept.id} AND " +
		"s.year = ?2)"
	)
    public Set<Staff> getRelevantStaff(@Param("dept") OrganisationUnit dept, String Year);
 */

}
