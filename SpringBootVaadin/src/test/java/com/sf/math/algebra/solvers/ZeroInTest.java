/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.math.algebra.solvers;

import com.helger.commons.math.MathHelper;
import java.math.BigDecimal;
import java.util.function.Function;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;

import java.math.RoundingMode;

import com.sf.math.algebra.Polynomial;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author OFeseniuk
 */
public class ZeroInTest {

    private static final int MAX_PRECISION_FOUND = 420; // 520
    private static final int TEST_PRECISION = MAX_PRECISION_FOUND / 10; 
    
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
        Double eps = IntervalSolver.DOUBLE_MAX_PRECISION;
        ZeroIn instance = new ZeroIn();
        Double result = instance.find(f, ax, bx, eps);
        Assert.assertEquals(String
            .format("Solution with given precission %f is not found", 10 * eps), 
            expResult, result, 10 * eps);
        
        f = x -> Math.tanh(x);
        result = instance.find(f, ax, bx, eps);
        Assert.assertEquals(String
            .format("Solution with given precission %f is not found", 10 * eps), 
            0, result, 10 * eps);
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
        Double eps = IntervalSolver.DOUBLE_MAX_PRECISION;
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
        final int scale = TEST_PRECISION;
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
        
        double expResult1 = (4 - Math.sqrt(13))/3;        
        f = x -> (BigDecimal)new Polynomial(Stream.of(1, -8, 3)
                    .map(BigDecimal::new)
                    .collect(Collectors.toList()))
                .value(x);
        a = new BigDecimal( -3.6666666666666665);
        b = new BigDecimal(1.3333333333);
        eps = new BigDecimal(3).scaleByPowerOfTen(-10);
        result = instance.find(f, a, b, eps);
        Assert.assertEquals(String
                .format("Solution with given precission %s is not found", 
                        eps.toString()), 
            expResult1, result.doubleValue(), eps.doubleValue());
        
        Polynomial p = Polynomial.fromRoots(java.util.Arrays.asList(new BigDecimal(2), 
                new BigDecimal(-1), new BigDecimal(3)));
        BigDecimal expResult2 = new BigDecimal(-1);        
        f = x -> MathHelper.toBigDecimal(p.value(x));
        a = new BigDecimal( -3.6666666666666665);
        b = new BigDecimal(1.3333333333);
        eps = new BigDecimal(3).scaleByPowerOfTen(-40);
        result = instance.find(f, a, b, eps);
        Assert.assertTrue(String
                .format("Solution with given precission %s is not found", 
                        eps.toString()), 
            expResult2.subtract(result).abs().multiply(BigDecimal.TEN).compareTo(eps) <= 0);
    }

    /**
     * Test of find method, of class ZeroIn.
     */
    @Test
    public void testFind_MaxForBigDecimal() {
        System.out.println("testFind_MaxForBigDecimal");
        final int scale = TEST_PRECISION;
        BigDecimal expResult = IntervalSolver.THREE.divide(new BigDecimal(17), 
                scale, RoundingMode.CEILING);        
        Function<BigDecimal, BigDecimal> f = x -> x.subtract(expResult).pow(2)
            .add(expResult.divide(BigDecimal.TEN))
            .negate();
        BigDecimal a = BigDecimal.ZERO;
        BigDecimal b = expResult.add(BigDecimal.TEN);
        BigDecimal eps = BigDecimal.ONE.scaleByPowerOfTen(-scale);
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
        final int scale = TEST_PRECISION;
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

    /**
     * Test of find method, of class ZeroIn.
     */
    @Test
    public void testFind_MinimumForBigDecimal() {
        System.out.println("testFind_MinimumForBigDecimal");
        final int scale = TEST_PRECISION;
        BigDecimal expResult = IntervalSolver.THREE.divide(new BigDecimal(17), 
                scale, RoundingMode.CEILING);        
        Function<BigDecimal, BigDecimal> f = x -> x.subtract(expResult).pow(2)
            .add(BigDecimal.ONE);
        BigDecimal a = BigDecimal.ZERO;
        BigDecimal b = expResult.add(BigDecimal.TEN);
        BigDecimal eps = BigDecimal.ONE.divide(BigDecimal.TEN).pow(scale);
        ZeroIn instance = new ZeroIn();
        BigDecimal result = instance.find(f, a, b, eps);
        Assert.assertTrue(String
                .format("Minimum with given precission %s is not found",
                        eps.scaleByPowerOfTen(1).toString()),
                expResult.subtract(result).abs().compareTo(eps) <= 0);
    }
    
}
