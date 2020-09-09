/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.math.algebra.solvers;

import com.helger.commons.math.MathHelper;
import com.sf.math.number.Complex;
import java.math.BigDecimal;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author OFeseniuk
 * solves equation f = 0 at guess point, f, f' given with Newton's technique
 */
@Service
public class NewtonSolver implements DerivativeSolver {    
    Logger LOG = LoggerFactory.getLogger(NewtonSolver.class);

    @Override
    public Double find(Function<Double, Double> f, Function<Double, Double> dF, 
            double guess, double eps) {
        double x0 = guess;
        double x;
        try {
            for (int i = 0; i < IntervalSolver.N_ITERATIONS; i++) {
                x = x0 - f.apply(x0) / dF.apply(x0);
                if (Math.abs(x - x0) <= eps || x == Double.NaN) {
                    break;
                }
                x0 = x;
            }            
        } catch (ArithmeticException e) {
            LOG.warn("ArithmeticException in NewtonSolver.find(Double)", e);
        }
        return x0;
    }

    @Override
    public Complex find(Function<Complex, Complex> f, 
            Function<Complex, Complex> dF, Complex guess, BigDecimal eps) {
        Complex x0 = guess;
        Complex x;
        try {
            for (int i = 0; i < IntervalSolver.N_ITERATIONS; i++) {
                x0 = x0.roundBigDecimalToPrecision(eps);
                x = x0.minus(f.apply(x0).div(dF.apply(x0)));
                if (MathHelper.toBigDecimal(x.minus(x0).abs2())
                        .compareTo(eps.pow(2)) <= 0) {
                    break;
                }
                x0 = x;
            }
        } catch (ArithmeticException e) {
            LOG.warn("ArithmeticException in NewtonSolver.find(BigDecimal)", e);
        }
        return x0;    
    }
    
}
