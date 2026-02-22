package se.uu.ebc.bemanning.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import se.uu.ebc.bemanning.entity.utils.TEActivity;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface TEActivityRepo extends JpaRepository<TEActivity, Long>, JpaSpecificationExecutor<TEActivity>{

    // findById(Long id) is inherited from JpaRepository and returns Optional<Course>
    Optional<TEActivity> findByTeText(String teText);
}
