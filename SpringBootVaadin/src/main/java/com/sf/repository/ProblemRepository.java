package com.sf.repository;

import com.sf.back.entities.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author sf
 */
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    
}
