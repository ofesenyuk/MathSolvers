/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.math.algebra.solvers;

import com.helger.commons.math.MathHelper;
import com.sf.VaadinMathSolverApplication;
import com.sf.math.algebra.Polynomial;
import com.sf.math.number.Complex;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author OFeseniuk
 */
@SpringBootTest(classes = VaadinMathSolverApplication.class)
public class PolynomialSolverIntervalTest {
    
    private static final double MINIMAL_DOUBLE_PRECISION_FOR_CLOSE_ROOTS = 1E-8;
    private static final BigDecimal 
            MINIMAL_BIG_DECIMAL_PRECISION_FOR_CLOSE_ROOTS 
            = BigDecimal.ONE.scaleByPowerOfTen(-15);

    @Autowired
    private PolynomialSolver solver;
    
    @Autowired
    IntervalSolver intervalSolver;
    
    @Autowired
    DerivativeSolver derivativeSolver;

    @BeforeAll
    public static void setUpClass() throws Exception {
    }

    @AfterAll
    public static void tearDownClass() throws Exception {
    }

    @BeforeEach
    public void setUp() throws Exception {
    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    /**
     * Test of findRoots method, of class PolynomialSolverInterval.
     */
    @Test
    public void testFindRoots_real() {
        System.out.println("testFindRoots_real"); 
        final double precision = 1E-10;
        Function<List<Number>, List<Number>> f = roots -> {      
            Polynomial p = Polynomial.fromRoots(roots);
            return solver.findRoots(p, precision);
        };
        ComparatorTestUtils.compareArraysWithDoublePrecision(Arrays.asList(1), f, precision);
        ComparatorTestUtils.compareArraysWithDoublePrecision(Arrays.asList(1, 2), f, precision);
        ComparatorTestUtils.compareArraysWithDoublePrecision(Arrays.asList(1, 2, 3), f, precision);
        ComparatorTestUtils.compareArraysWithDoublePrecision(Arrays.asList(1, 2, 3, 4), f, 
                precision);
        // [24, -50, 35, -10, 1]
        // [-50, 70, -30, 4]
        // [70, -60, 12] {(60-sqrt(240))/24 (60+sqrt(240))/24} = {1.85, 3.15}
        // [-60, 24] -> 2.5
        ComparatorTestUtils.compareArraysWithDoublePrecision(Arrays.asList(1, 2, 3, 4, 5), f, 
                precision);
        // [274, -450, 255, -60, 5]
        // [-450, 510, -180, 20]
        // [510, -360, 60] = [17, -12, 2] {(6 - sqrt(2))/2, (6 + sqrt(2))/2} = {3.707,2.293}
        // [-360, 120] -> 3
        findRootsBigDecimalTest(Arrays.asList(new BigDecimal(2), 
                new BigDecimal(-1), new BigDecimal(3)), null);
        // [6, 1, -4, 1]
        // [1, -8, 3] {(4 - sqrt(13))/3, (4 + sqrt(13))/3} = {0.131, 2.535}
        // [-8, 6] -> 4/3=1.33333
    }
    
    @Test
    public void testFindMultipleRoots_real() {
        System.out.println("testFindMultipleRoots_real");
        final double precision = 1E-5;
        Function<List<Number>, List<Number>> f = roots -> {
            Polynomial p = Polynomial.fromRoots(roots);
            return solver.findRoots(p, precision);
        };
        final List<Number> rootsD = Arrays.asList(1, 1, 3, 4);
        // [12, -31, 27, -9, 1] = [-1, 1]*[-12, 19, -8, 1] = [-1,1]*[-1,1]*[12, -7, 1]
        // [-31, 54, -27, 4] = [-1,1]*[31,-23,4] -> {1, (23\pm\sqrt(33))/8} = {1, 3.59307, 2.15693}
        // [54, -54, 12] = [27,-27,6] -> [(27\pm\sqrt(81))/12]= [1.5, 3]
        ComparatorTestUtils.compareArraysWithDoublePrecision(rootsD, f,
                precision);
        findRootsBigDecimalTest(Arrays.asList(new BigDecimal(2),
                new BigDecimal(2), new BigDecimal(3)), null);
    }
    
    @Test
    public void testFindCloseRoots_real() {
        System.out.println("testFindCloseRoots_real");
        final double precision = MINIMAL_DOUBLE_PRECISION_FOR_CLOSE_ROOTS;
        try {
        Function<List<Number>, List<Number>> f = roots -> {
            Polynomial p = Polynomial.fromRoots(roots);
            return solver.findRoots(p, precision);
        };
        List<Number> rootsD = Arrays.asList(1, 1 + precision * 20);
        ComparatorTestUtils.compareArraysWithDoublePrecision(rootsD, f,
                precision);
        rootsD = Arrays.asList(1, 1 + precision * 20, 3, 4);
            ComparatorTestUtils.compareArraysWithDoublePrecision(rootsD, f,
                    precision);
            
        final BigDecimal precisionB 
            = MINIMAL_BIG_DECIMAL_PRECISION_FOR_CLOSE_ROOTS;
        final BigDecimal r1 = BigDecimal.ONE;
            BigDecimal r2 = precisionB.multiply(new BigDecimal(20)).add(r1);
            findRootsBigDecimalTest(Arrays.asList(r1,
                    r2),
                precisionB);
        findRootsBigDecimalTest(Arrays.asList(r1,
                r2,
                new BigDecimal(5)),
                precisionB);
        findRootsBigDecimalTest(Arrays.asList(r1,
                r2,
                new BigDecimal(2),
                new BigDecimal(5)),
                precisionB);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    @Test
    public void testFindCloseRoots_complex() {
        System.out.println("testFindCloseRoots_complex");
        final double precision = MINIMAL_DOUBLE_PRECISION_FOR_CLOSE_ROOTS;
        try {
        Function<List<Number>, List<Number>> f = roots -> {
            Polynomial p = Polynomial.fromRoots(roots);
            return solver.findRoots(p, precision);
        };
        List<Number> rootsD = Arrays.asList(
                new Complex(1, -1), 
                new Complex(1, 1), 
                new Complex(1 + precision * 20, 1), 
                new Complex(1 + precision * 20, -1));
        
        Polynomial p = Polynomial.fromRoots(rootsD);
        p = new Polynomial(p.getCoefficients().stream()
            .map(Number::doubleValue)
            .collect(Collectors.toList()));
        List<Number> result = solver.findRoots(p, precision);
        ComparatorTestUtils.compareArraysWithDoublePrecision(
            rootsD.stream()
                .map(Complex::new)
                .map(Complex::getX)
                .collect(Collectors.toList()), 
                x -> result.stream()
                    .map(Complex::new)
                    .map(Complex::getX)
                    .collect(Collectors.toList()),
                precision);
        
        rootsD = Arrays.asList(
                new Complex(1, -1), 
                new Complex(1, 1), 
                new Complex(1, 1 + precision * 20), 
                new Complex(1, -1 - precision * 20));
        
        p = Polynomial.fromRoots(rootsD);
        p = new Polynomial(p.getCoefficients().stream()
            .map(Number::doubleValue)
            .collect(Collectors.toList()));
        List<Number> resultI = solver.findRoots(p, precision);
        ComparatorTestUtils.compareArraysWithDoublePrecision(
            rootsD.stream()
                .map(Complex::new)
                .map(Complex::getX)
                .collect(Collectors.toList()), 
                x -> resultI.stream()
                    .map(Complex::new)
                    .map(Complex::getX)
                    .collect(Collectors.toList()),
                precision);
        ComparatorTestUtils.compareArraysWithDoublePrecision(
            rootsD.stream()
                .map(Complex::new)
                .map(Complex::getY)
                .collect(Collectors.toList()), 
                x -> resultI.stream()
                    .map(Complex::new)
                    .map(Complex::getY)
                    .collect(Collectors.toList()),
                precision);
//        rootsD = Arrays.asList(1, 1 + precision * 20, 3, 4);
//            ComparatorTestUtils.compareArraysWithDoublePrecision(rootsD, f,
//                    precision);
//            
        final BigDecimal precisionB 
            = MINIMAL_BIG_DECIMAL_PRECISION_FOR_CLOSE_ROOTS;
        final Complex r1 = new Complex(BigDecimal.ONE, BigDecimal.ONE);
        List<Number> cRoots = Arrays.asList(r1,
                r1.conjugate(),
                r1.plus(precisionB.multiply(new BigDecimal(20))), 
                r1.plus(precisionB.multiply(new BigDecimal(20))).conjugate());
        p = Polynomial.fromRoots(cRoots);
        List<Number> resultB = solver.findRoots(p, precisionB);
        ComparatorTestUtils.compareArraysWithBigDecimalPrecision(
                Complex.getX(cRoots), 
                x -> Complex.getX(resultB), 
                precisionB);
        ComparatorTestUtils.compareArraysWithBigDecimalPrecision(
                Complex.getY(cRoots), 
                x -> Complex.getY(resultB), 
                precisionB);
        
        final Complex defl = new Complex(0, precisionB)
                .multiply(new BigDecimal(30));
        cRoots = Arrays.asList(r1,
                r1.conjugate(),
                r1.plus(defl), 
                r1.plus(defl).conjugate());
        p = Polynomial.fromRoots(cRoots);
        List<Number> resultBI = solver.findRoots(p, precisionB);
        ComparatorTestUtils.compareArraysWithBigDecimalPrecision(
                Complex.getX(cRoots), 
                x -> Complex.getX(resultBI), 
                precisionB);
        ComparatorTestUtils.compareArraysWithBigDecimalPrecision(
                Complex.getY(cRoots), 
                x -> Complex.getY(resultBI), 
                precisionB);
//        findRootsBigDecimalTest(Arrays.asList(r1,
//                precisionB.multiply(new BigDecimal(20)).add(r1), 
//                new BigDecimal(5)),
//                precisionB);
//        findRootsBigDecimalTest(Arrays.asList(r1,
//                precisionB.multiply(new BigDecimal(20)).add(r1), 
//                new BigDecimal(2),
//                new BigDecimal(5)),
//                precisionB);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Test of findRoots method, of class PolynomialSolverInterval.
     */
    @Test
    public void testFindRoots_2complex() throws Exception {
        System.out.println("testFindRoots_2complex");
        findAndCompareComplexRootsWithDoublePrecision(Arrays
            .asList(new Complex(1, 1), new Complex(1, -1)));
        // [2, -2, 1]
        // [-2, 2] -> {1}
    }
    @Test
    public void testFindRoots_4complex() throws Exception {
        System.out.println("testFindRoots_4complex");
        try {
        findAndCompareComplexRootsWithDoublePrecision(Arrays
            .asList(new Complex(1, 1), new Complex(1, -1),
                new Complex(1, 7), new Complex(1, -7)));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw e;
        }
        // [100, -104, 56, -4, 1]
        // [-104.0, 112.0, -12.0, 4.0] = [-26, 28, -3, 1] = [-1, 1] * [26, -2, 1] -> {1, 1 pm 5i} 
        // [112, -24, 12] = [28, -6, 3] -> {(3 pm sqrt(-75)) / 3} = {(3 pm i8.66) / 3} = {1 pm 2.89i}
    }
    @Test
    public void testFindRootsWithBigDecimalPrecision_4complex() throws Exception {
        try {
        System.out.println("testFindRootsWithBigDecimalPrecision_4complex");
        // [100, -104, 56, -4, 1]
        // [-104.0, 112.0, -12.0, 4.0] = [-26, 28, -3, 1] = [-1, 1] * [26, -2, 1] -> {1, 1 pm 5i} 
        // [112, -24, 12] = [28, -6, 3] -> {(3 pm sqrt(-75)) / 3} = {(3 pm i8.66) / 3} = {1 pm 2.89i}
        findAndCompareComplexRootsWithBigDecimalPrecision(Arrays
            .asList(new Complex(1, 1), new Complex(1, -1),
                new Complex(1, 7), new Complex(1, -7)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    /**
     * Test of findRootForLinear method, of class PolynomialSolverInterval.
     */
    @Test
    public void testFindRootForLinear() {
        System.out.println("findRootForLinear");
        Double expResult = 1.0;
        double coeff1 = 1.0;
        List<Number> coeffs = Arrays.asList(-expResult * coeff1, coeff1);
        Polynomial p = new Polynomial(coeffs);
        Number precision = 10;
        Number result = new PolynomialSolverInterval(null, null)
                .findRootForLinear(p, precision);
        assertEquals(expResult, result, "Root of liear equation is not found");
        
        BigDecimal expResultB = BigDecimal.ONE.scaleByPowerOfTen(-12);
        BigDecimal coeffB = new BigDecimal("123.321");
        List<Number> coeffsB 
                = Arrays.asList(expResultB.multiply(coeffB).negate(), coeffB);
        Polynomial pB = new Polynomial(coeffsB);
        Number resultB = new PolynomialSolverInterval(null, null)
                .findRootForLinear(pB, precision);
        assertEquals(expResultB, resultB, "Root of liear equation is not found");
    }
    
    @Test
    public void testFindRootForLinear_forIllegalArgument() {
        System.out.println("testFindRootForLinear_forIllegalArgument");
        List<Number> coeffs = Arrays.asList(1, 2, 3, 4);
        Polynomial p = new Polynomial(coeffs);
        assertThrows(IllegalArgumentException.class,
                () -> new PolynomialSolverInterval(null, null)
                        .findRootForLinear(p, null));
}

    /**
     * Test of findComplexRootInUpperRectangle method, of class PolynomialSolverInterval.
     */
    @Test
    public void testFindRootInUpperRectangle_Double() {
        System.out.println("testFindRootInUpperRectangle_Double");
        double ax = 1;
        double bx = 4;
        double ay = 3;
        double by = 7;
        double precision = 5E-7;
        Complex root1 = new Complex((ax + bx) / 2, (ay + by) / 2);
        Complex root2 = root1.conjugate();
        Polynomial p = Polynomial.fromRoots(Arrays.asList(root1, root2));
        final PolynomialSolverInterval instance 
            = new PolynomialSolverInterval(intervalSolver, derivativeSolver);
        final Pair<Pair<Number, Number>, Pair<Number, Number>> ab 
            = Pair.of(Pair.of(ax, ay), Pair.of(bx, by));
        Number result = instance.findRootInUpperRectangle(ab, precision, p);
        assertEquals(root1.getX().doubleValue(), result.doubleValue(),
                precision, "x-part of root is not found");
        assertEquals(root1.getY().doubleValue(), new Complex(result).getY().doubleValue(),
            precision, "y-part of root is not found");
        
        final double realRoot = ax + (ax + bx) / 3;        
        p = Polynomial.fromRoots(Arrays.asList(root1, root2, realRoot));
        double resultD 
            = (double) instance.findRootInUpperRectangle(ab, precision, p);
        assertEquals(realRoot, resultD, precision, "real root is not found");
    }
    @Test
    public void testFindRootInUpperRectangle_BigDecimal() {
        System.out.println("testFindRootInUpperRectangle_BigDecimal");
        BigDecimal ax = BigDecimal.ONE;
        BigDecimal bx = new BigDecimal(4);
        BigDecimal ay = new BigDecimal(3);
        BigDecimal by = new BigDecimal(7);
        BigDecimal precision = new BigDecimal("5E-7");
        Complex root1 = new Complex(ax.add(bx).divide(new BigDecimal(2)), 
                ay.add(by).divide(new BigDecimal(2)));
        Complex root2 = root1.conjugate();
        Polynomial p = Polynomial.fromRoots(Arrays.asList(root1, root2));
        final Pair<Pair<Number, Number>, Pair<Number, Number>> ab 
                = Pair.of(Pair.of(ax, ay), Pair.of(bx, by));
        final PolynomialSolverInterval instance 
            = new PolynomialSolverInterval(intervalSolver, derivativeSolver);
        Number result = instance.findRootInUpperRectangle(ab, precision, p);
        Complex delta = root1.minus(new Complex(result));
        Comparable deltaX = (Comparable) delta.getX();
        assertTrue(deltaX.compareTo(precision) <= 0, "Root's real part is irregular");
        Comparable deltaY = (Comparable) delta.getX();
        assertTrue(deltaY.compareTo(precision) <= 0, "Root's imaginary part is irregular");
        
        final BigDecimal twoSeventh = new BigDecimal(2)
                .divide(new BigDecimal(7), 20, RoundingMode.CEILING);        
        BigDecimal realRoot = ax.add(twoSeventh.multiply(ax.add(bx)));
        p = Polynomial.fromRoots(Arrays.asList(root1, root2, realRoot));
        result = instance.findRootInUpperRectangle(ab, precision, p);
        assertTrue(realRoot.subtract(MathHelper.toBigDecimal(result)).abs()
                    .compareTo(precision) <= 0,
                "Correct real root is not found");
    }

    /**
     * Test of getRootUpperLimitCauchy method, of class PolynomialSolverInterval.
     */
    @Test
    public void testGetRootUpperLimitCauchy() {
        System.out.println("testGetRootUpperLimitCauchy");
        List<Number> coefficients = Stream.of(6, 5, 1)
                .map(c -> c * 24)
                .collect(Collectors.toList());
        PolynomialSolverInterval instance 
                = new PolynomialSolverInterval(null, null);
        Double expResult = 7.0; // 1 + max(6/1, 5/1) > {|-2|, |-3|}
        Double result = instance.getRootUpperLimitCauchy(coefficients);
        assertEquals(expResult, result);
    }

    private void findRootsBigDecimalTest(List<Number> expResult, 
            BigDecimal eps) {
        Polynomial p = Polynomial.fromRoots(expResult);
        BigDecimal precision = eps != null ? eps 
                : new BigDecimal(3).scaleByPowerOfTen(-10);
        Object[] result = solver.findRoots(p, precision)
                .stream()
                .map(Complex::new)
                .map(Complex::getX)
                .map(MathHelper::toBigDecimal)
                .sorted()
                .toArray();
        Object[] expResultSorted = expResult
                .stream()
                .sorted()
                .toArray();

        for (int i = 0; i < expResultSorted.length; i++) {
            final Object expectedRes = expResultSorted[i];
            final Object res = result[i];
            final BigDecimal delta = ((BigDecimal) expectedRes)
                    .subtract((BigDecimal) res)
                    .abs();
            assertTrue(delta.compareTo(precision) <= 0,
                    () -> String.format("Root is not found with given precision: %f != %f %s != %s",
                            expectedRes, res, delta.toString(), precision.toString()));
        }
    }

    private void findAndCompareComplexRootsWithDoublePrecision(
            List<Number> expResult) {
        Polynomial p = Polynomial.fromRoots(expResult);
        double precision = 7E-6;
        p = new Polynomial(p.getCoefficients()
            .stream()
            .map(Number::doubleValue)
            .collect(Collectors.toList()));
        final List<Number> expResultR = expResult
            .stream()
            .map(Number::doubleValue)
            .collect(Collectors.toList());
        final List<Number> expResultI = expResult
            .stream()
            .map(r -> new Complex(r).getY())
            .collect(Collectors.toList());
        List<Number> result = solver.findRoots(p, precision);
        ComparatorTestUtils.compareArraysWithDoublePrecision(expResultR,
                x -> result
                        .stream()
                        .map(Number::doubleValue)
                        .map(r -> (Number) r)
                        .collect(Collectors.toList()),
                precision
        );
        ComparatorTestUtils.compareArraysWithDoublePrecision(expResultI,
                x -> result
                        .stream()
                        .map(r -> new Complex(r).getY())
                        .collect(Collectors.toList()),
                precision
        );
    }

    private void findAndCompareComplexRootsWithBigDecimalPrecision(
            List<Number> expResult) throws Exception {
        Polynomial p = Polynomial.fromRoots(expResult);
        Number precision = new BigDecimal(6).scaleByPowerOfTen(-41);
        p = new Polynomial(p.getCoefficients()
            .stream()
            .map(MathHelper::toBigDecimal)
            .collect(Collectors.toList()));
        final List<Number> expResultR = expResult
            .stream()
            .map(Complex::new)
            .map(Complex::getX)
            .collect(Collectors.toList());
        final List<Number> expResultI = expResult
            .stream()
            .map(Complex::new)
            .map(Complex::getY)
            .collect(Collectors.toList());
        try {
        List<Number> result = solver.findRoots(p, precision);
        ComparatorTestUtils.compareArraysWithBigDecimalPrecision(expResultR,
                x -> result
                        .stream()
                        .map(r -> new Complex(r).getX())
                        .collect(Collectors.toList()),
                (BigDecimal) precision
        );
        ComparatorTestUtils.compareArraysWithBigDecimalPrecision(expResultI,
                x -> result
                        .stream()
                        .map(r -> new Complex(r).getY())
                        .collect(Collectors.toList()),
                (BigDecimal) precision
        );
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }
}
