/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sf.math.number

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author sf
 */
@RunWith(SpringRunner.class)
class ComplexTest {
    
    /**
     * Test of plus method, of class Complex.
     */
    @Test
    public void testPlus() {
        System.out.println("testPlus");
        final Complex expResult = new Complex(x: 2, y: 2);
        Complex result = new Complex(x: 1, y: 2) + 1;
        assertEquals("x-s are not equal", expResult.x, result.x);
        assertEquals("y-s are not equal", expResult.y, result.y);
        
        result = new Complex(x: 1, y: 2) + 1.0;
        assertEquals("x-s are not equal", expResult.x, result.x, 0.0);
        assertEquals("y-s are not equal", expResult.y, result.y);
        
        result = new Complex(x: 1, y: 2) + BigDecimal.ONE;
        assertEquals("x-s are not equal", expResult.x, result.x, 0.0);
        assertEquals("y-s are not equal", expResult.y, result.y);
        
        result = new Complex(x: 1, y: -1) + new Complex(x: 1, y: 3);
        assertEquals("x-s are not equal", expResult.x, result.x);
        assertEquals("y-s are not equal", expResult.y, result.y);
    }
    
    /**
     * Test of minus method, of class Complex.
     */
    @Test
    public void testMinus() {
        System.out.println("testMinus");
        final Complex expResult = new Complex(x: 1, y: 2);
        Complex result = new Complex(x: 2, y: 2) - 1;
        assertEquals("x-s are not equal", expResult.x, result.x);
        assertEquals("y-s are not equal", expResult.y, result.y);
        
        result = new Complex(x: 2, y: 2) - 1.0;
        assertEquals("x-s are not equal", expResult.x, result.x, 0.0);
        assertEquals("y-s are not equal", expResult.y, result.y);
        
        result = new Complex(x: 2, y: 2) - BigDecimal.ONE.divide(BigDecimal.ONE);
        assertEquals("x-s are not equal", expResult.x, result.x, 0.0);
        assertEquals("y-s are not equal", expResult.y, result.y);
        
        result = new Complex(x: 3, y: -1) - new Complex(x: 2, y: -3);
        assertEquals("x-s are not equal", expResult.x, result.x);
        assertEquals("y-s are not equal", expResult.y, result.y);
    }
    
    /**
     * Test of multiply method, of class Complex.
     */
    @Test
    public void testMultiply() {
        System.out.println("testMultiply");
        final Complex expResult = new Complex(x: 6, y: -9);
        Complex result = new Complex(x: 2, y: -3) * 3;
        assertEquals("x-s are not equal", expResult.x, result.x);
        assertEquals("y-s are not equal", expResult.y, result.y);
        
        result = new Complex(x: 2, y: -3) * 3.0;
        assertEquals("x-s are not equal", expResult.x, result.x, 0.0);
        assertEquals("y-s are not equal", expResult.y, result.y, 0.0);
        
        result = new Complex(x: 2, y: -3) * new BigDecimal(3.0);
        assertEquals("x-s are not equal", expResult.x, result.x, 0.0);
        assertEquals("y-s are not equal", expResult.y, result.y, 0.0);
        
        expResult = new Complex(x: 2, y: -10);
        result = new Complex(x: 3, y: -2) * new Complex(x: 2, y: -2);
        assertEquals("x-s are not equal", expResult.x, result.x);
        assertEquals("y-s are not equal", expResult.y, result.y);
        
        int expResult1 = 104;
        int result1 = expResult * expResult.conjugate();
        assertEquals("conjugate does not work properly", expResult1, result1);
    }
    
    /**
     * Test of negative method, of class Complex.
     */
    @Test
    public void testNegative() {
        System.out.println("testNegative");
        Complex result = -new Complex(x: -2, y: 3);
        final Complex expResult = new Complex(x: 2, y: -3);
        assertEquals("x-s are not equal", expResult.x, result.x, 0.0);
        assertEquals("y-s are not equal", expResult.y, result.y, 0.0);
    }
    /**
     * Test of divide method, of class Complex.
     */
    @Test
    public void testDivide() {
        System.out.println("testDivide");
        final Complex expResult = new Complex(x: 2, y: -3);
        Complex result = new Complex(x: 6, y: -9) / 3;
        assertEquals("x-s are not equal", expResult.x, result.x, 0.0);
        assertEquals("y-s are not equal", expResult.y, result.y, 0.0);
        
        result = new Complex(x: 6, y: -9) / 3.0;
        assertEquals("x-s are not equal", expResult.x, result.x, 0.0);
        assertEquals("y-s are not equal", expResult.y, result.y, 0.0);
        
        result = new Complex(x: 6, y: -9) / new BigDecimal(3);
        assertEquals("x-s are not equal", expResult.x, result.x, 0.0);
        assertEquals("y-s are not equal", expResult.y, result.y, 0.0);
        
        expResult = new Complex(x: 1.25, y: 0.25);
        result = new Complex(x: 3, y: -2) / new Complex(x: 2, y: -2); // (6 + 4)/8, (-4 + 6)/8
        assertEquals("x-s are not equal", expResult.x, result.x, 0.0);
        assertEquals("y-s are not equal", expResult.y, result.y, 0.0);
    }
}
