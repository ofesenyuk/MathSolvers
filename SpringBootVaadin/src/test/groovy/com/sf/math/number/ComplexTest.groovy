/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sf.math.number

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author sf
 */
//@RunWith(SpringRunner.class)
@SpringBootTest
class ComplexTest {
    
    /**
     * Test of plus method, of class Complex.
     */
    @Test
    public void testPlus() {
        System.out.println("testPlus");
        final Complex expResult = new Complex(x: 2, y: 2);
        Complex result = new Complex(x: 1, y: 2) + 1;
        assertEquals(expResult.x, result.x, "x-s are not equal");
        assertEquals(expResult.y, result.y, "y-s are not equal");
        
        result = new Complex(x: 1, y: 2) + 1.0;
        assertEquals(expResult.x, result.x, 0.0, "x-s are not equal");
        assertEquals(expResult.y, result.y, 0.0, "y-s are not equal");
        
        result = new Complex(x: 1, y: 2) + BigDecimal.ONE;
        assertEquals(expResult.x, result.x, 0.0, "x-s are not equal");
        assertEquals(expResult.y, result.y, 0.0, "y-s are not equal");
        
        result = new Complex(x: 1, y: -1) + new Complex(x: 1, y: 3);
        assertEquals(expResult.x, result.x, "x-s are not equal");
        assertEquals(expResult.y, result.y, "y-s are not equal");
    }
    
    /**
     * Test of minus method, of class Complex.
     */
    @Test
    public void testMinus() {
        System.out.println("testMinus");
        final Complex expResult = new Complex(x: 1, y: 2);
        Complex result = new Complex(x: 2, y: 2) - 1;
        assertEquals(expResult.x, result.x, "x-s are not equal");
        assertEquals(expResult.y, result.y, "y-s are not equal");
        
        result = new Complex(x: 2, y: 2) - 1.0;
        assertEquals(expResult.x, result.x, 0.0, "x-s are not equal");
        assertEquals(expResult.y, result.y, 0.0, "y-s are not equal");
        
        result = new Complex(x: 2, y: 2) - BigDecimal.ONE.divide(BigDecimal.ONE);
        assertEquals(expResult.x, result.x, 0.0, "x-s are not equal");
        assertEquals(expResult.y, result.y, 0.0, "y-s are not equal");
        
        result = new Complex(x: 3, y: -1) - new Complex(x: 2, y: -3);
        assertEquals(expResult.x, result.x, "x-s are not equal");
        assertEquals(expResult.y, result.y, "y-s are not equal");
    }
    
    /**
     * Test of multiply method, of class Complex.
     */
    @Test
    public void testMultiply() {
        System.out.println("testMultiply");
        Complex expResult = new Complex(x: 6, y: -9);
        Complex result = new Complex(x: 2, y: -3) * 3;
        assertEquals(expResult.x, result.x, "x-s are not equal");
        assertEquals(expResult.y, result.y, "y-s are not equal");
        
        result = new Complex(x: 2, y: -3) * 3.0;
        assertEquals(expResult.x, result.x, 0.0, "x-s are not equal");
        assertEquals(expResult.y, result.y, 0.0, "y-s are not equal");
        
        result = new Complex(x: 2, y: -3) * new BigDecimal(3.0);
        assertEquals(expResult.x, result.x, 0.0, "x-s are not equal");
        assertEquals(expResult.y, result.y, 0.0, "y-s are not equal");
        
        expResult = new Complex(x: 2, y: -10);
        result = new Complex(x: 3, y: -2) * new Complex(x: 2, y: -2);
        assertEquals(expResult.x, result.x, "x-s are not equal");
        assertEquals(expResult.y, result.y, "y-s are not equal");
        
        int expResult1 = 104;
        int result1 = expResult * expResult.conjugate();
        assertEquals(expResult1, result1, "conjugate does not work properly");
    }
    
    /**
     * Test of negative method, of class Complex.
     */
    @Test
    public void testNegative() {
        System.out.println("testNegative");
        Complex result = -new Complex(x: -2, y: 3);
        final Complex expResult = new Complex(x: 2, y: -3);
        assertEquals(expResult.x, result.x, 0.0, "conjugate does not work properly");
        assertEquals(expResult.y, result.y, 0.0, "y-s are not equal");
    }
    
   /**
     * Test of divide method, of class Complex.
     */
    @Test
    public void testDivide() {
        System.out.println("testDivide");
        Complex expResult = new Complex(x: 2, y: -3);
        Complex result = new Complex(x: 6, y: -9) / 3;
        assertEquals(expResult.x, result.x, 0.0, "x-s are not equal");
        assertEquals(expResult.y, result.y, 0.0, "y-s are not equal");
        
        result = new Complex(x: 6, y: -9) / 3.0;
        assertEquals(expResult.x, result.x, 0.0, "x-s are not equal");
        assertEquals(expResult.y, result.y, 0.0, "y-s are not equal");
        
        result = new Complex(x: 6, y: -9) / new BigDecimal(3);
        assertEquals(expResult.x, result.x, 0.0, "x-s are not equal");
        assertEquals(expResult.y, result.y, 0.0, "y-s are not equal");
        
        expResult = new Complex(x: 1.25, y: 0.25);
        result = new Complex(x: 3, y: -2) / new Complex(x: 2, y: -2); // (6 + 4)/8, (-4 + 6)/8
        assertEquals(expResult.x, result.x, 0.0, "x-s are not equal");
        assertEquals(expResult.y, result.y, 0.0, "y-s are not equal");
    }
    
    @Test
    public void testRoundBigDecimalToPrecision() {
        System.out.println("testRoundBigDecimalToPrecision");
        BigDecimal eps = new BigDecimal(30).scaleByPowerOfTen(-3);
        Complex result = new Complex(123.45678901, 98.76543210987654)
            .roundBigDecimalToPrecision(eps);
        Complex expResult = new Complex(new BigDecimal("123.457"), 
            new BigDecimal("98.765"));
        assertEquals(expResult.x, result.x, "x-s are not equal");
        assertEquals(expResult.y, result.y, "y-s are not equal");
    }
}
