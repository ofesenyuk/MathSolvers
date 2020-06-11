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

import com.sf.math.algebra.solvers.IntervalSolver;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 *
 * @author OFeseniuk
 */
public class ZeroInTest {
    
    public ZeroInTest() {
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
     * Test of find method, of class ZeroIn.
     */
    @Test
    public void testFind_RootForDouble() {
        System.out.println("testFind_RootForDouble");
        Double expResult = 1.0 / 7.0;
        Function<Double, Double> f = x -> x * x - expResult * expResult;
        Double ax = 0.0;
        Double bx = 2.0;
        Double eps = IntervalSolver.DOUBLE_MAX_PRECISSION;
        ZeroIn instance = new ZeroIn();
        Double result = instance.find(f, ax, bx, eps);
        Assert.assertEquals(String
            .format("Solution with given precission %f is not found", 2 * eps), 
            expResult, result, 10 * eps);
    }

    /**
     * Test of find method, of class ZeroIn.
     */
    @Test
    public void testFind_MinForDouble() {
        System.out.println("testFind_RootForDouble");
        Double expResult = Math.PI / 2;
        Function<Double, Double> f = x -> Math.pow(x - expResult, 2)  + 0.1;
        Double ax = expResult - 0.01;
        Double bx = expResult + 0.01;
        Double eps = 1E-9;
        ZeroIn instance = new ZeroIn();
        Double result = instance.find(f, ax, bx, eps);
        Assert.assertEquals(String
            .format("Maximum with given precission %f is not found", 2 * eps), 
            expResult, result, 10 * eps);
    }

    /**
     * Test of find method, of class ZeroIn.
     */
    @Test
    public void testFind_RootInMinimumForDouble() {
        System.out.println("testFind_RootInMinimumForDouble");
        Double expResult = Math.PI / 2;
        Function<Double, Double> f = x -> Math.cos(x);
        Double ax = 0.0;
        Double bx = Math.PI;
        Double eps = IntervalSolver.DOUBLE_MAX_PRECISSION;
        ZeroIn instance = new ZeroIn();
        Double result = instance.find(f, ax, bx, eps);
        Assert.assertEquals(String
            .format("Maximum with given precission %f is not found", 2 * eps), 
            expResult, result, 10 * eps);
    }

    /**
     * Test of find method, of class ZeroIn.
     */
    @Test
    public void testFind_RootForBigDecimal() {
        System.out.println("testFind_RootForBigDecimal");
        final int scale = 420;
        BigDecimal expResult = IntervalSolver.THREE.divide(new BigDecimal(17), 
                scale, RoundingMode.CEILING);        
        Function<BigDecimal, BigDecimal> f = x -> 
                (x.pow(2)).subtract(expResult.pow(2));
        BigDecimal a = BigDecimal.ZERO;
        BigDecimal b = expResult.add(BigDecimal.TEN);
        BigDecimal eps = BigDecimal.ONE.divide(BigDecimal.TEN).pow(scale);
        ZeroIn instance = new ZeroIn();
        BigDecimal result = instance.find(f, a, b, eps);
        Assert.assertEquals(String
                .format("Solution with given precission %s is not found", 
                        eps.toString()), 
            expResult, result);
    }

    /**
     * Test of find method, of class ZeroIn.
     */
    @Test
    public void testFind_MaxForBigDecimal() {
        System.out.println("testFind_MaxForBigDecimal");
        final int scale = 420;
        BigDecimal expResult = IntervalSolver.THREE.divide(new BigDecimal(17), 
                scale, RoundingMode.CEILING);        
        Function<BigDecimal, BigDecimal> f = x -> x.subtract(expResult).pow(2)
            .add(expResult.divide(BigDecimal.TEN))
            .negate();
        BigDecimal a = BigDecimal.ZERO;
        BigDecimal b = expResult.add(BigDecimal.TEN);
        BigDecimal eps = BigDecimal.ONE.divide(BigDecimal.TEN).pow(scale);
        ZeroIn instance = new ZeroIn();
        BigDecimal result = instance.find(f, a, b, eps);
        Assert.assertTrue(String
                .format("Solution with given precission %s is not found", 
                        eps.scaleByPowerOfTen(1).toString()), 
            expResult.subtract(result).abs().compareTo(eps) <= 0);
    }

    /**
     * Test of find method, of class ZeroIn.
     */
    @Test
    public void testFind_RootInMaximumForBigDecimal() {
        System.out.println("testFind_MaxForBigDecimal");
        final int scale = 420;
        BigDecimal expResult = IntervalSolver.THREE.divide(new BigDecimal(17), 
                scale, RoundingMode.CEILING);        
        Function<BigDecimal, BigDecimal> f = x -> x.subtract(expResult).pow(2)
            .negate();
        BigDecimal a = BigDecimal.ZERO;
        BigDecimal b = expResult.add(BigDecimal.TEN);
        BigDecimal eps = BigDecimal.ONE.divide(BigDecimal.TEN).pow(scale);
        ZeroIn instance = new ZeroIn();
        BigDecimal result = instance.find(f, a, b, eps);
        Assert.assertTrue(String
                .format("Solution with given precission %s is not found",
                        eps.scaleByPowerOfTen(1).toString()),
                expResult.subtract(result).abs().compareTo(eps) <= 0);
    }
    
}
