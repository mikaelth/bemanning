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
public interface OrganisationUnitRepo extends JpaRepository<OrganisationUnit, Long>, JpaSpecificationExecutor<OrganisationUnit>{

    public OrganisationUnit findById(Long id);  

	@Query("SELECT ou FROM OrganisationUnit AS ou WHERE ou.abbreviation = ?1")
	public OrganisationUnit findByAbbreviation(String abbreviation);    
 
 

}
