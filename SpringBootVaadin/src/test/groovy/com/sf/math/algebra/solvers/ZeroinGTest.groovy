/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sf.math.algebra.solvers

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringRunner;

import com.sf.math.algebra.solvers.ZeroinG;
import com.sf.math.algebra.solvers.IntervalSolver;

/**
 *
 * @author OFeseniuk
 */
@RunWith(SpringRunner.class)
class ZeroinGTest {
    
    private static final int MAX_PRECISSION_FOUND = 200; 
    
    /**
     * Test of find method, of class ZeroIn.
     */
//    @Test
    public void testFind_RootForDoubleG() {
        System.out.println("testFind_RootForDouble_G");        
        Double expResult = BigDecimal.ONE.divide(new BigDecimal("7"), 100, RoundingMode.CEILING).doubleValue();
        def f = {x -> x * x - expResult * expResult};
        Double ax = 0.0;
        Double bx = 2.0;
        Double eps = IntervalSolver.DOUBLE_MAX_PRECISSION;
        ZeroinG instance = new ZeroinG();
        Double result = instance.find(f, ax, bx, eps);
        Double delta = result - expResult;
        println "expResult = $expResult result = $result delta = $delta";
        Assert.assertEquals(String
            .format("Solution with given precission %f is not found", 10 * eps), 
            expResult, result, 10 * eps);
    }
    
    /**
     * Test of find method, of class ZeroIn.
     */
    @Test
    public void testFind_RootForBigDecimalG() {
        System.out.println("testFind_RootForBigDecimalG");
        final int scale = MAX_PRECISSION_FOUND;
        BigDecimal expResult = IntervalSolver.THREE.divide(new BigDecimal(17), 
            scale, RoundingMode.CEILING);        
        def f = {x -> (x.pow(2)).subtract(expResult.pow(2))};
        BigDecimal a = BigDecimal.ZERO;
        BigDecimal b = expResult.add(BigDecimal.TEN);
        BigDecimal eps = BigDecimal.ONE.divide(BigDecimal.TEN).pow(scale);
        ZeroinG instance = new ZeroinG();
        BigDecimal result = instance.find(f, a, b, eps);
        def delta = (result - expResult).toString().split("E")[1];
        Assert.assertTrue(String
            .format("Solution with given precission %s is not found", 
                eps.toString()), 
            (expResult - result).abs().compareTo(eps) < 0);
    }
}

