/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.math.algebra.solvers;

import java.util.List;

import com.sf.math.algebra.Polynomial;
import java.math.BigDecimal;
import java.util.Deque;

/**
 *
 * @author OFeseniuk
 */
public interface PolynomialSolver {
    
    /**
     * finds given polynomial's roots
     * 
     * @param p, polynomial, (p = 0 is equation to be solved)
     * @param precision, precision
     * @return roots as List
     */
    public List<Number> findRoots(Polynomial p, Number precision);
}
