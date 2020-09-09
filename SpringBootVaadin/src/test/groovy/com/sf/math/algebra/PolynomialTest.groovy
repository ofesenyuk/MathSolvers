/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sf.math.algebra

import java.math.RoundingMode;
import org.junit.Test;
import org.junit.Assert;
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
        Assert.assertEquals([1], p1.coefficients);
        p1 = new Polynomial(Collections.emptyList());
        Assert.assertEquals([1], p1.coefficients);
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
        Assert.assertEquals(INEQUAL_COEFFICIENTS, expResult.coefficients, 
            result.coefficients);
        Assert.assertFalse("coefficients' pointers are equal", expResult == result);
        
        
        result = new Polynomial([-5, 1, -7, 6]) + new Polynomial([7, 1, 10, -6]);
        Assert.assertEquals(INEQUAL_COEFFICIENTS, expResult.coefficients, 
            result.coefficients);
        
        result = new Polynomial([new BigDecimal(-5), 1, -7, new BigDecimal(6)]) + new Polynomial([7, 1, 10, -6]);
        List<BigDecimal> expCoeffs = expResult.coefficients.collect{c -> new BigDecimal(c)};
        List<BigDecimal> resCoeffs = result.coefficients.collect{c -> new BigDecimal(c)};
        Assert.assertEquals(INEQUAL_COEFFICIENTS, expCoeffs, resCoeffs);
    }
    
    /**
     * Test of plus null method, of class Polynomial.
     */
    @Test
    public void testPlusNull() {
        System.out.println("testPlusNull");
        List<Integer> coefficients = [1, 2, 3];
        Polynomial p1 = new Polynomial(coefficients);        
        Assert.assertEquals(INEQUAL_COEFFICIENTS, 
            [coefficients[0] + 1] + coefficients[1..<coefficients.size()], 
            (p1 + new Polynomial(null)).coefficients); 
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
        Assert.assertEquals(INEQUAL_COEFFICIENTS, expResult.coefficients, 
            result.coefficients);
        Assert.assertFalse("coefficients' pointers are equal", expResult == result);
        
        
        result = new Polynomial([-2, 1, -7, 6]) - new Polynomial([-6, -1, -10, 6]);
        Assert.assertEquals(INEQUAL_COEFFICIENTS, expResult.coefficients, result.coefficients);
        
        result = new Polynomial([new BigDecimal(-2), 1, -7, new BigDecimal(6)]) - new Polynomial([-6, -1, -10, 6]);
        List<BigDecimal> expCoeffs = expResult.coefficients.collect{c -> new BigDecimal(c)};
        List<BigDecimal> resCoeffs = result.coefficients.collect{c -> new BigDecimal(c)};
        Assert.assertEquals(INEQUAL_COEFFICIENTS, expCoeffs, resCoeffs);
    }
    
    /**
     * Test of null minus method, of class Polynomial.
     */
    @Test
    public void testMinusNull() {
        System.out.println("testMinusNull");
        List<Integer> coefficients = [5, 2, 3];
        Polynomial p1 = new Polynomial(coefficients);
        Assert.assertEquals(INEQUAL_COEFFICIENTS, 
            [1 - coefficients[0]] + (coefficients[1..<coefficients.size()]).collect{-it},
            (new Polynomial([]) - p1).coefficients);
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
        Assert.assertEquals(INEQUAL_COEFFICIENTS, expResult.coefficients, result.coefficients);        
        
        final Polynomial p2 = new Polynomial([3, 4]);
        expResult = new Polynomial([3, 10, 8]);
        Polynomial result1 = p1 * p2;
        Assert.assertEquals(INEQUAL_COEFFICIENTS, expResult.coefficients, result1.coefficients);
        
        result = new Polynomial([-2, 1]) * new Polynomial([4, 2, 1]);
        expResult = new Polynomial([-8, 0, 0, 1]);
        Assert.assertEquals(INEQUAL_COEFFICIENTS, expResult.coefficients, result.coefficients);        
        
        // to debug use simplier numbers
        List<Number> roots = [2.2, new Complex(3.1, 2.5), new Complex(3.1, -2.5), 0]; 
        //        List<Number> roots = [2, new Complex(1, 3)]; // x^2 - (3 + 3i)x + (2 + 6i)
        p1 = Polynomial.fromRoots(roots);
        try {
        roots.each(){ 
            Number val1 = p1.value(it);
            if (val1 instanceof Complex) {
                Assert.assertEquals(ZERO_AT_ROOT, 0, val1.x, 0); 
                Assert.assertEquals(ZERO_AT_ROOT, 0, val1.y, 0); 
            } else {
                Assert.assertEquals(ZERO_AT_ROOT, 0, val1, 0); 
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
        
        Assert.assertEquals(INEQUAL_COEFFICIENTS, p1.coefficients, 
            (new Polynomial([]) * p1).coefficients);
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
        range.each{Assert.assertEquals(INEQUAL_COEFFICIENTS, expResult.coefficients[it], result.coefficients[it], 0)};        
        
        final int remainder = 0;
        p1 = Polynomial.fromRoots([1, 2, 3]) + remainder; // x*x*x - 6*x*x +11*x - 6 + 5
        final Polynomial p2 = Polynomial.fromRoots([2, 3]); // x*x - 5*x + 6
        expResult = Polynomial.fromRoots([1]);
        result = p1 / p2;
        range.each{Assert.assertEquals(INEQUAL_COEFFICIENTS, expResult.coefficients[it], result.coefficients[it], 0)};
        
        
    }
    
    /**
     * Test of div null method, of class Polynomial.
     */
    @Test
    public void testDivNull() {
        System.out.println("testDivNull");
        Polynomial p1 = new Polynomial([1, 2]);
        
        Assert.assertEquals(INEQUAL_COEFFICIENTS, p1.coefficients, 
            p1.div(new Polynomial([])).coefficients.collect{it.intValue()});
    }
    
    /**
     * Test of fromRoots method, of class Polynomial.
     */
    @Test
    public void testFromRoots() {
        System.out.println("testFromRoots");
        Assert.assertEquals(new Polynomial(null).coefficients, Polynomial.fromRoots(null).coefficients);
        Assert.assertEquals(new Polynomial(null).coefficients, Polynomial.fromRoots([]).coefficients);
        int root = 3;
        Assert.assertEquals(ZERO_AT_ROOT, 0, Polynomial.fromRoots([root]).value(root), 0);
    }
    
    /**
     * Test of negative method, of class Polynomial.
     */
    @Test
    public void testNegative() {
        System.out.println("testNegative");
        List<Number> coeffs = [1, 2, 3, 4];
        Polynomial p = new Polynomial(coeffs);
        Assert.assertEquals("Polynomial is not negated", -coeffs, (-p).coefficients);
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
        Assert.assertEquals("Polynomial derivative is has errors", dCoeffs, p.derivative().coefficients);
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
        Assert.assertTrue("Roots are not recognized", roots.every{p.isRoot(it, precision)});
        List<Number> cRoots = [new BigDecimal(Math.PI), new Complex(BigDecimal.TEN, BigDecimal.ONE), new Complex(BigDecimal.TEN, -BigDecimal.ONE)];
        Polynomial cP = Polynomial.fromRoots(cRoots);
        BigDecimal cPrecision = BigDecimal.ONE.scaleByPowerOfTen(-24);
        Assert.assertTrue("Complex roots are not recognized", cRoots.every{cP.isRoot(it, cPrecision)});
        Assert.assertTrue("Roots are not recognized", roots.every{p.isRoot(it, cPrecision)});
        List<Number> cRoots2 =[new Complex(1.1, 1.4), new Complex(1.1, -1.4)]
        Polynomial cP2 = Polynomial.fromRoots(cRoots2);
        Assert.assertTrue("Complex roots are not recognized", cRoots2.every{cP2.isRoot(it, precision)});
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
        Assert.assertTrue("All coefficients must be of Double class", complexP.coefficients.every{!it || it instanceof Double});
        
        coeffs = [BigDecimal.ONE, 1, null, 0, BigDecimal.TEN, 1]
        p = new Polynomial(coeffs);
        complexP = p.unifyCoefficientsTypes();
        Assert.assertTrue("All coefficients must be of BigDecimal class", complexP.coefficients.every{!it || it instanceof BigDecimal});
        
        
        coeffs = [new Complex(1.1, 12), new Complex(1), null, new Complex(BigDecimal.ONE, BigDecimal.TEN), BigDecimal.TEN, 1]
        p = new Polynomial(coeffs);
        complexP = p.unifyCoefficientsTypes();
        Assert.assertTrue("All coefficients must be of Complex class", complexP.coefficients.every{!it || it instanceof Complex});
        
        coeffs = [new Complex(1.1, 12), new Complex(1), 2, null, new Complex(BigDecimal.ZERO, BigDecimal.ZERO), BigDecimal.ZERO, 0]
        p = new Polynomial(coeffs);
        complexP = p.unifyCoefficientsTypes();
        Assert.assertTrue("All coefficients must be of Complex class", complexP.coefficients.every{!it || it instanceof Complex});
     
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
        Assert.assertTrue("All coefficients must be of Complex class", complexP.coefficients.every{it == null || it instanceof Complex});
     
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
        Assert.assertEquals("MultipliedPrecision is not calculated normally", 
            expectedValue, multipliedPrecision, 0.1);
     
        precision = new BigDecimal(precision);
        multipliedPrecision = p.getMultipliedPrecision(precision);
        Assert.assertEquals("MultipliedPrecision is not calculated normally", 
            expectedValue, multipliedPrecision.doubleValue(), 0.1);
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
        Assert.assertTrue("Coefficients are still complex", 
            coeffs.every{!(it instanceof Complex)});
    }
}

