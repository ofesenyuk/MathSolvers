/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sf.math.algebra

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.sf.math.number.Complex;
import com.sf.math.algebra.Polynomial;
/**
 *
 * @author sf
 */
@RunWith(SpringRunner.class)
class PolynomialTest {
    
    private static final INEQUAL_COEFFICIENTS = "coefficients are not equal";
    private static final ZERO_AT_ROOT = "polynomial value is not zero at its root";
    
    /**
     * Test of constructor, of class Polynomial.
     */
    @Test
    public void testConstructor() {
        println "testConstructor";
        Polynomial p1 = new Polynomial(null);
        assertNull("coefficients are not null for null", p1.coefficients);
        p1 = new Polynomial(Collections.emptyList());
        assertNull("coefficients are not null for empty", p1.coefficients);
    }
    
    /**
     * Test of plus method, of class Polynomial.
     */
    @Test
    public void testPlus() {
        System.out.println("testPlus");
        List<Integer> coefficients = [1, 2, 3];
        Polynomial p1 = new Polynomial(coefficients);
        final Polynomial expResult = new Polynomial([2, 2, 3]);
        Polynomial result = p1 + 1;
        assertEquals(INEQUAL_COEFFICIENTS, expResult.coefficients, 
            result.coefficients);
        assertFalse("coefficients' pointers are equal", expResult == result);
        
        
        result = new Polynomial([-5, 1, -7, 6]) + new Polynomial([7, 1, 10, -6]);
        assertEquals(INEQUAL_COEFFICIENTS, expResult.coefficients, 
            result.coefficients);
        
        result = new Polynomial([new BigDecimal(-5), 1, -7, new BigDecimal(6)]) + new Polynomial([7, 1, 10, -6]);
        List<BigDecimal> expCoeffs = expResult.coefficients.collect{c -> new BigDecimal(c)};
        List<BigDecimal> resCoeffs = result.coefficients.collect{c -> new BigDecimal(c)};
        assertEquals(INEQUAL_COEFFICIENTS, expCoeffs, resCoeffs);
    }
    
    /**
     * Test of plus null method, of class Polynomial.
     */
    @Test(expected = NullPointerException.class)
    public void testPlusNull() {
        System.out.println("testPlusNull");
        List<Integer> coefficients = [1, 2, 3];
        Polynomial p1 = new Polynomial(coefficients);        
        p1 + new Polynomial(null);
    }
    
    /**
     * Test of minus method, of class Polynomial.
     */
    @Test
    public void testMinus() {
        System.out.println("testMinus");
        List<Integer> coefficients = [5, 2, 3];
        Polynomial p1 = new Polynomial(coefficients);
        final Polynomial expResult = new Polynomial([4, 2, 3]);
        Polynomial result = p1 - 1;
        assertEquals(INEQUAL_COEFFICIENTS, expResult.coefficients, 
            result.coefficients);
        assertFalse("coefficients' pointers are equal", expResult == result);
        
        
        result = new Polynomial([-2, 1, -7, 6]) - new Polynomial([-6, -1, -10, 6]);
        assertEquals(INEQUAL_COEFFICIENTS, expResult.coefficients, result.coefficients);
        
        result = new Polynomial([new BigDecimal(-2), 1, -7, new BigDecimal(6)]) - new Polynomial([-6, -1, -10, 6]);
        List<BigDecimal> expCoeffs = expResult.coefficients.collect{c -> new BigDecimal(c)};
        List<BigDecimal> resCoeffs = result.coefficients.collect{c -> new BigDecimal(c)};
        assertEquals(INEQUAL_COEFFICIENTS, expCoeffs, resCoeffs);
    }
    
    /**
     * Test of null minus method, of class Polynomial.
     */
    @Test(expected = NullPointerException.class)
    public void testMinusNull() {
        System.out.println("testMinusNull");
        List<Integer> coefficients = [5, 2, 3];
        Polynomial p1 = new Polynomial(coefficients);
        
        new Polynomial([]) - p1;
    }
    
    /**
     * Test of multiply method, of class Polynomial.
     */
    @Test
    public void testMultiply() {
        System.out.println("testMultiply");
        Polynomial p1 = new Polynomial([1, 2]);
        Polynomial expResult = new Polynomial([3, 6]);
        Polynomial result = p1 * 3;
        assertEquals(INEQUAL_COEFFICIENTS, expResult.coefficients, result.coefficients);        
        
        final Polynomial p2 = new Polynomial([3, 4]);
        expResult = new Polynomial([3, 10, 8]);
        Polynomial result1 = p1 * p2;
        assertEquals(INEQUAL_COEFFICIENTS, expResult.coefficients, result1.coefficients);
        
        result = new Polynomial([-2, 1]) * new Polynomial([4, 2, 1]);
        expResult = new Polynomial([-8, 0, 0, 1]);
        assertEquals(INEQUAL_COEFFICIENTS, expResult.coefficients, result.coefficients);        
        
        List<Number> roots = [2.2, new Complex(x: 3.1, y: 2.5), new Complex(x: 3.1, y: -2.5), 0];
//        List<Number> roots = [2, new Complex(x: 1, y: 3)]; // x^2 - (3 + 3i)x + (2 + 6i)
        p1 = Polynomial.fromRoots(roots);
        roots.each(){ 
            Number val1 = p1.value(it);
            println 'root ' + it + ' val ' + val1;
            if (val1 instanceof Complex) {
                assertEquals(ZERO_AT_ROOT, 0, val1.x, 0); 
                assertEquals(ZERO_AT_ROOT, 0, val1.y, 0); 
            } else {
                assertEquals(ZERO_AT_ROOT, 0, val1, 0); 
            }
        }
        
    }
    
    /**
     * Test of multiply null method, of class Polynomial.
     */
    @Test(expected = NullPointerException.class)
    public void testMultiplyNull() {
        System.out.println("testMultiplyNull");
        Polynomial p1 = new Polynomial([1, 2]);
        
        new Polynomial([]) * p1;
    }
    
    /**
     * Test of fromRoots method, of class Polynomial.
     */
    @Test
    public void testFromRoots() {
        System.out.println("testFromRoots");
        assertEquals(new Polynomial(null).coefficients, Polynomial.fromRoots(null).coefficients);
        assertEquals(new Polynomial(null).coefficients, Polynomial.fromRoots([]).coefficients);
        int root = 3;
        assertEquals(ZERO_AT_ROOT, 0, Polynomial.fromRoots([root]).value(root));
    }
}
