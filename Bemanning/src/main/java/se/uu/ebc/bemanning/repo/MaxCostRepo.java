package se.uu.ebc.bemanning.repo;

import javax.persistence.TypedQuery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import se.uu.ebc.bemanning.entity.MaxCost;
import se.uu.ebc.bemanning.enums.EmploymentType;

import java.util.Set;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface MaxCostRepo extends JpaRepository<MaxCost, Long>, JpaSpecificationExecutor<MaxCost>{

    @Query("SELECT m FROM MaxCost m WHERE m.id.position = ?1 and m.id.year = ?2")
    public MaxCost findByCategoryYear(EmploymentType category, String year);  
 
}
