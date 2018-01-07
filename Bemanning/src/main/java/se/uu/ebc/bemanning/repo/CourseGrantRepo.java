package se.uu.ebc.bemanning.repo;

import javax.persistence.TypedQuery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import se.uu.ebc.bemanning.entity.CourseGrant;

import java.util.Set;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface CourseGrantRepo extends JpaRepository<CourseGrant, Long>, JpaSpecificationExecutor<CourseGrant>{

    public CourseGrant findById(Long id);  
 
}
