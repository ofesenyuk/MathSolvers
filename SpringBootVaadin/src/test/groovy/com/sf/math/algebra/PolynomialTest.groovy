/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sf.math.algebra

import java.math.RoundingMode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.sf.math.number.Complex;
import com.sf.math.algebra.Polynomial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author sf
 */
//@RunWith(SpringRunner.class)
@SpringBootTest
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
        assert [1] == p1.coefficients;
        p1 = new Polynomial(Collections.emptyList());
        assert [1] == p1.coefficients;
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
        assert expResult.coefficients == result.coefficients : INEQUAL_COEFFICIENTS;
        assert expResult != result : "coefficients' pointers are equal";
        
        
        result = new Polynomial([-5, 1, -7, 6]) + new Polynomial([7, 1, 10, -6]);
        assert expResult.coefficients == result.coefficients : INEQUAL_COEFFICIENTS;
        
        result = new Polynomial([new BigDecimal(-5), 1, -7, new BigDecimal(6)]) + new Polynomial([7, 1, 10, -6]);
        List<BigDecimal> expCoeffs = expResult.coefficients.collect{c -> new BigDecimal(c)};
        List<BigDecimal> resCoeffs = result.coefficients.collect{c -> new BigDecimal(c)};
        assert expCoeffs == resCoeffs : INEQUAL_COEFFICIENTS;
    }
    
    /**
     * Test of plus null method, of class Polynomial.
     */
    @Test
    public void testPlusNull() {
        System.out.println("testPlusNull");
        List<Integer> coefficients = [1, 2, 3];
        Polynomial p1 = new Polynomial(coefficients);        
        assert [coefficients[0] + 1] + coefficients[1..<coefficients.size()]
            == (p1 + new Polynomial(null)).coefficients : INEQUAL_COEFFICIENTS; 
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
        assert expResult.coefficients == result.coefficients : INEQUAL_COEFFICIENTS
        assert expResult != result : "coefficients' pointers are equal"
        
        
        result = new Polynomial([-2, 1, -7, 6]) - new Polynomial([-6, -1, -10, 6]);
        assert expResult.coefficients == result.coefficients : INEQUAL_COEFFICIENTS
        
        result = new Polynomial([new BigDecimal(-2), 1, -7, new BigDecimal(6)]) - new Polynomial([-6, -1, -10, 6]);
        List<BigDecimal> expCoeffs = expResult.coefficients.collect{c -> new BigDecimal(c)};
        List<BigDecimal> resCoeffs = result.coefficients.collect{c -> new BigDecimal(c)};
        assert expCoeffs == resCoeffs : INEQUAL_COEFFICIENTS
    }
    
    /**
     * Test of null minus method, of class Polynomial.
     */
    @Test
    public void testMinusNull() {
        System.out.println("testMinusNull");
        List<Integer> coefficients = [5, 2, 3];
        Polynomial p1 = new Polynomial(coefficients);
        assertEquals([1 - coefficients[0]] + (coefficients[1..<coefficients.size()]).collect{-it},
            (new Polynomial([]) - p1).coefficients,
            INEQUAL_COEFFICIENTS);
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
        assertEquals(expResult.coefficients, result.coefficients, INEQUAL_COEFFICIENTS);        
        
        final Polynomial p2 = new Polynomial([3, 4]);
        expResult = new Polynomial([3, 10, 8]);
        Polynomial result1 = p1 * p2;
        assertEquals(expResult.coefficients, result1.coefficients, INEQUAL_COEFFICIENTS);
        
        result = new Polynomial([-2, 1]) * new Polynomial([4, 2, 1]);
        expResult = new Polynomial([-8, 0, 0, 1]);
        assertEquals(expResult.coefficients, result.coefficients, INEQUAL_COEFFICIENTS);        
        
        // to debug use simplier numbers
        List<Number> roots = [2.2, new Complex(3.1, 2.5), new Complex(3.1, -2.5), 0]; 
        //        List<Number> roots = [2, new Complex(1, 3)]; // x^2 - (3 + 3i)x + (2 + 6i)
        p1 = Polynomial.fromRoots(roots);
        try {
        roots.each(){ 
            Number val1 = p1.value(it);
            if (val1 instanceof Complex) {
                assertEquals(0, val1.x, 0, ZERO_AT_ROOT); 
                assertEquals(0, val1.y, 0, ZERO_AT_ROOT); 
            } else {
                assertEquals(0, val1, 0, ZERO_AT_ROOT); 
            }
        }
        } catch(NumberFormatException e) {
            e.printStackTrace();
            throw new Exception(e);
        }
        
    }
    
    /**
     * Test of multiply null method, of class Polynomial.
     */
    @Test
    public void testMultiplyNull() {
        System.out.println("testMultiplyNull");
        Polynomial p1 = new Polynomial([1, 2]);
        
        assertEquals(p1.coefficients, 
            (new Polynomial([]) * p1).coefficients, INEQUAL_COEFFICIENTS);
    }
    
    /**
     * Test of div method, of class Polynomial.
     */
    @Test
    public void testDiv() {
        System.out.println("testDiv");
        Polynomial p1 = new Polynomial([6, -12]);
        Polynomial expResult = new Polynomial([2, -4]);
        Polynomial result = p1 / 3;
        def range = 0..<expResult.coefficients.size();
        range.each{assertEquals(expResult.coefficients[it], result.coefficients[it], 0, INEQUAL_COEFFICIENTS)};        
        
        final int remainder = 0;
        p1 = Polynomial.fromRoots([1, 2, 3]) + remainder; // x*x*x - 6*x*x +11*x - 6 + 5
        final Polynomial p2 = Polynomial.fromRoots([2, 3]); // x*x - 5*x + 6
        expResult = Polynomial.fromRoots([1]);
        result = p1 / p2;
        range.each{assertEquals(expResult.coefficients[it], result.coefficients[it], 0, INEQUAL_COEFFICIENTS)};
        
        
    }
    
    /**
     * Test of div null method, of class Polynomial.
     */
    @Test
    public void testDivNull() {
        System.out.println("testDivNull");
        Polynomial p1 = new Polynomial([1, 2]);
        
        assertEquals(p1.coefficients, 
            p1.div(new Polynomial([])).coefficients.collect{it.intValue()},
            INEQUAL_COEFFICIENTS);
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
        assertEquals(0, Polynomial.fromRoots([root]).value(root), 0, ZERO_AT_ROOT);
    }
    
    /**
     * Test of negative method, of class Polynomial.
     */
    @Test
    public void testNegative() {
        System.out.println("testNegative");
        List<Number> coeffs = [1, 2, 3, 4];
        Polynomial p = new Polynomial(coeffs);
        assertEquals(-coeffs, (-p).coefficients, "Polynomial is not negated");
    }
    
    /**
     * Test of derivative method, of class Polynomial.
     */
    @Test
    public void testDerivative() {
        System.out.println("testDerivative");
        List<Number> coeffs = [3, 2, 3, 4];
        List<Number> dCoeffs = [2, 6, 12];
        Polynomial p = new Polynomial(coeffs);
        assertEquals(dCoeffs, p.derivative().coefficients, "Polynomial derivative is has errors");
    }
    
    /**
     * Test of isRoot method, of class Polynomial.
     */
    @Test
    public void testIsRoot() {
        System.out.println("testIsRoot");
        List<Number> roots = [5.1234567890123, 100000000.123456765432, -123432123.54323454];
        Polynomial p = Polynomial.fromRoots(roots);
        float precision = 1.0E-10f
        assertTrue(roots.every{p.isRoot(it, precision)}, "Roots are not recognized");
        List<Number> cRoots = [new BigDecimal(Math.PI), new Complex(BigDecimal.TEN, BigDecimal.ONE), new Complex(BigDecimal.TEN, -BigDecimal.ONE)];
        Polynomial cP = Polynomial.fromRoots(cRoots);
        BigDecimal cPrecision = BigDecimal.ONE.scaleByPowerOfTen(-24);
        assertTrue(cRoots.every{cP.isRoot(it, cPrecision)}, "Complex roots are not recognized");
        assertTrue(roots.every{p.isRoot(it, cPrecision)}, "Roots are not recognized");
        List<Number> cRoots2 =[new Complex(1.1, 1.4), new Complex(1.1, -1.4)]
        Polynomial cP2 = Polynomial.fromRoots(cRoots2);
        assertTrue(cRoots2.every{cP2.isRoot(it, precision)}, "Complex roots are not recognized");
    }
    
    /**
     * Test of unifyCoefficientsTypes method, of class Polynomial.
     */
    @Test
    public void testUnifyCoefficientsTypes() {
        System.out.println("testUnifyCoefficientsTypes");
        List<Number> coeffs = [Double.valueOf(5.1234567890123), 1, null, Double.valueOf(0.0), -123432123.54323454f];
        Polynomial p = new Polynomial(coeffs);
        Polynomial complexP = p.unifyCoefficientsTypes();
        assertTrue(complexP.coefficients.every{!it || it instanceof Double}, "All coefficients must be of Double class");
        
        coeffs = [BigDecimal.ONE, 1, null, 0, BigDecimal.TEN, 1]
        p = new Polynomial(coeffs);
        complexP = p.unifyCoefficientsTypes();
        assertTrue(complexP.coefficients.every{!it || it instanceof BigDecimal}, "All coefficients must be of BigDecimal class");
                
        coeffs = [new Complex(1.1, 12), new Complex(1), null, new Complex(BigDecimal.ONE, BigDecimal.TEN), BigDecimal.TEN, 1]
        p = new Polynomial(coeffs);
        complexP = p.unifyCoefficientsTypes();
        assertTrue(complexP.coefficients.every{!it || it instanceof Complex}, "All coefficients must be of Complex class");
        
        coeffs = [new Complex(1.1, 12), new Complex(1), 2, null, new Complex(BigDecimal.ZERO, BigDecimal.ZERO), BigDecimal.ZERO, 0]
        p = new Polynomial(coeffs);
        complexP = p.unifyCoefficientsTypes();
        assertTrue(complexP.coefficients.every{!it || it instanceof Complex}, "All coefficients must be of Complex class");
     
    }
    
    /**
     * Test of toComplex method, of class Polynomial.
     */
    @Test
    public void testToComplex() {
        System.out.println("testToComplex");
        List<Number> coeffs = [new Complex(1.1, 12), new Complex(1), null, new Complex(BigDecimal.ONE, BigDecimal.TEN), BigDecimal.TEN, 1]
        Polynomial p = new Polynomial(coeffs);
        Polynomial complexP = p.toComplex();
        assertTrue(complexP.coefficients.every{it == null || it instanceof Complex}, "All coefficients must be of Complex class");
     
    }
    
    /**
     * Test of getMultipliedPrecision method, of class Polynomial.
     */
    @Test
    public void testGetMultipliedPrecision() {
        System.out.println("testGetMultipliedPrecision");
        List<Number> coeffs = [1, 2, 3];
        Number precision = 0.7;
        Polynomial p = new Polynomial(coeffs);
        Number multipliedPrecision = p.getMultipliedPrecision(precision);
        Number expectedValue = precision / (1 + 1 + 2 * 1 + 3 * 2);
        assertEquals(expectedValue, multipliedPrecision, 0.1, \
            "MultipliedPrecision is not calculated normally");
     
        precision = new BigDecimal(precision);
        multipliedPrecision = p.getMultipliedPrecision(precision);
        assertEquals(expectedValue, multipliedPrecision.doubleValue(), 0.1, \
            "MultipliedPrecision is not calculated normally");
    }
    
    /**
     * Test of getMultipliedPrecision method, of class Polynomial.
     */
    @Test
    public void toRealCoefficientIfPossible() {
        System.out.println("toRealCoefficientIfPossible");
        List<Number> roots = [new Complex(1, 1), new Complex(1, -1)];
        Polynomial p = Polynomial.fromRoots(roots);
        List<Number> coeffs = p.coefficients;
        assertTrue(coeffs.every{!(it instanceof Complex)}, \
            "Coefficients are still complex");
    }
}

