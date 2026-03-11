package se.uu.ebc.bemanning.repo;

import jakarta.persistence.TypedQuery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import se.uu.ebc.bemanning.entity.Person;
import se.uu.ebc.bemanning.entity.staff.AkkaStaff;
import se.uu.ebc.bemanning.entity.OrganisationUnit;
import se.uu.ebc.bemanning.enums.EmploymentType;

import java.util.Set;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface AkkaStaffRepo extends JpaRepository<AkkaStaff, Long>, JpaSpecificationExecutor<AkkaStaff>{

	@Query("SELECT s FROM AkkaStaff AS s WHERE s.employeeNumber = ?1 and s.year = ?2")
	public Optional<AkkaStaff> findUserByEmployeeNumberAndYear(String employeeNumber, String year);

}
