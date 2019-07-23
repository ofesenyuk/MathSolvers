package com.sf.repository;

import com.sf.back.entities.Matrix;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author sf
 */
public interface MatrixRepository extends JpaRepository<Matrix, Long> {

    public List<Matrix> findByIsConditionAndParentProblemId(Boolean isCondition, 
                                                            Long id);
    
}
