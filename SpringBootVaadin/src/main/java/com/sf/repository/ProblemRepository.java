/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.repository;

import com.sf.back.entities.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author sf
 */
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    
}
