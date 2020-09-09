/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.math.algebra.solvers;

import java.math.BigDecimal;
import java.util.function.Function;

import com.sf.math.number.Complex;

/**
 *
 * @author sf
 * 
 * Solve transcendent equation, f = 0, at f and f' given
 */
public interface DerivativeSolver {
    
    static final int N_ITERATIONS = 10000;    
    static final double DOUBLE_MAX_PRECISION = 1E-16;    
    
    static final BigDecimal TWO = BigDecimal.ONE.add(BigDecimal.ONE);
    static final BigDecimal THREE = new BigDecimal(3);
            
    /**
     * Zero of a function near guess point.
     * @param f function
     * @param guess guess point
     * @param dF derivative function
     * @param eps precision
     * @return root found
     */
    Double find(final Function<Double, Double> f, 
            final Function<Double, Double> dF, 
            final double guess, final double eps);
    Complex find(final Function<Complex, Complex> f, 
            final Function<Complex, Complex> dF, 
            final Complex guess, final BigDecimal eps);
}
