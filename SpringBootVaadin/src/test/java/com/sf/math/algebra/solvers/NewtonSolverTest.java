/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.math.algebra.solvers;

import java.math.BigDecimal;
import java.util.function.Function;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;

import com.sf.math.number.Complex;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author OFeseniuk
 */
public class NewtonSolverTest {
    
    public NewtonSolverTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of find method, of class NewtonSolver.
     */
    @Test
    public void testFindNewtonRootForDouble() {
        System.out.println("testFindNewtonRootForDouble");
        Double expResult = 1.0 / 7.0;
        Function<Double, Double> f = x -> x * x - expResult * expResult;
        Function<Double, Double> dF = x -> 2 * x;
        double guess = 0.01;
        double eps = 1.0E-10;
        NewtonSolver instance = new NewtonSolver();
        Double result = instance.find(f, dF, guess, eps);
        Assert.assertEquals(expResult, result, eps);
        
        guess = expResult * 1000;
        result = instance.find(f, dF, guess, eps);
        Assert.assertEquals(expResult, result, eps);
        
        guess = expResult * 1000;
        f = x -> Math.sqrt(x - expResult);
        dF = x -> 1.0 / 2.0 / Math.sqrt(x - expResult);
        result = instance.find(f, dF, guess, eps);
        Assert.assertEquals(Double.NaN, result, eps);
        
        guess = expResult * 10;
        f = x -> Math.atan(x - expResult);
        dF = x -> 1.0 / (1.0 + Math.pow(x - expResult, 2));
        result = instance.find(f, dF, guess, eps);
        Assert.assertEquals(expResult, result, eps);
    }

    /**
     * Test of find method, of class NewtonSolver.
     */
    @Test
    public void testFindNewtonRootForComplex() {
        System.out.println("testFindNewtonRootForComplex");
        Complex expResult = new Complex(1.0 / 7.0);
        Function<Complex, Complex> f = x -> x.multiply(x).minus(expResult.multiply(expResult));
        Function<Complex, Complex> dF = x -> x.multiply(2);
        Complex guess = new Complex(0.01);
        BigDecimal eps = BigDecimal.ONE.scaleByPowerOfTen(-10);
        NewtonSolver instance = new NewtonSolver();
        Complex result = instance.find(f, dF, guess, eps);
        
        ComparatorTestUtils.compareArraysWithDoublePrecision(
                Collections.singletonList(expResult), 
                x -> Collections.singletonList(result), 
                eps.doubleValue());
    }
    
}
