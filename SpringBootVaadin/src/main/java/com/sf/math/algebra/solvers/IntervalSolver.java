/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.math.algebra.solvers;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 *
 * @author sf
 * 
 * Solves equation f = 0 in given interval
 */
public interface IntervalSolver {
    
    static final int N_ITERATIONS = 100;    
    static final double DOUBLE_MAX_PRECISION = 1E-16;    
    
    static final BigDecimal TWO = BigDecimal.ONE.add(BigDecimal.ONE);
    static final BigDecimal THREE = new BigDecimal(3);
            
    /**
     * Zero of a function in an interval.
     * @param f function
     * @param a from
     * @param b to
     * @param eps precision
     * @return root found
     */
    Double find(final Function<Double, Double> f, 
            final double a, final double b, final double eps);
    BigDecimal find(final Function<BigDecimal, BigDecimal> f, 
            final BigDecimal a, final BigDecimal b, final BigDecimal eps);
}
