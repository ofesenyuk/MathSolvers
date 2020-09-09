/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.math.algebra.solvers;

import com.helger.commons.math.MathHelper;
import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.stereotype.Service;

import com.sf.math.algebra.Polynomial;
import com.sf.math.number.Complex;
import java.util.Comparator;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;

/**
 *
 * @author OFeseniuk
 * 
 * finding polynomial root in interval of root limits determined by math theory
 */

@Service
public class PolynomialSolverInterval implements PolynomialSolver {
    static final String LINEAR_POLYNOMIAL_IS_EXPECTED 
            = "Only linear polynomial is expected ";
    
    static final double Y_MIN = 0.0;    
    
    private static final String NULL_ARGUMENT_OR_ZERO_PRECISION 
        = "Null argument or zero precision";
    private static final int DEFLECTION_FACTOR_FOR_CLOSE_ROOTS = 4;    
    private static final int ACCEPTABLE_DEFLECTION_PRECISION_FACTOR = 10;

    private Logger LOG 
        = LoggerFactory.getLogger(PolynomialSolverInterval.class);

    private final IntervalSolver intervalSolver;
    private final DerivativeSolver derivativeSolver;

    public PolynomialSolverInterval(
            IntervalSolver intervalSolver,
            DerivativeSolver derivativeSolver
            ) {
        this.intervalSolver = intervalSolver;
        this.derivativeSolver = derivativeSolver;
    }
    
    @Override
    public List<Number> findRoots(
            Polynomial p, Number precision
    ) {
        List<Number> coefficients = p.getCoefficients();
        int initialSize = coefficients.size();
        LOG.debug("findRoots for coefficients {} with precision {}", 
            coefficients, precision);
        
        if (initialSize == 2) {
            return Collections.singletonList(findRootForLinear(p,  precision));
        }
        
        Polynomial derivative = p.derivative();
        List<Number> dRoots = new ArrayList<>(findRoots(derivative, precision));
        List<Number> multipleRoots = dRoots
                .stream()
                .filter(r -> p.isRoot(r, precision))
                .collect(Collectors.toList());
        
        List<Number> foundRoots 
            = getCloseRootsSeparatedFromMultipleRoots(multipleRoots, p, 
                                                      precision);
        
        if (foundRoots.size() + 1 == initialSize) {
            return foundRoots;
        }
        
        Polynomial multipleRootsdivisor = Polynomial.fromRoots(foundRoots);
        
        Polynomial simpleRootsP = p.div(multipleRootsdivisor);
        
        if (simpleRootsP.getCoefficients().size() == 2) {
            foundRoots.add(findRootForLinear(simpleRootsP, precision));
            return foundRoots;
        }
        
        Double rootUpperLimit = getRootUpperLimitCauchy(coefficients);
        
        dRoots.removeAll(multipleRoots);
        List<Number> searchIntervalsMargins 
                = getSearchIntevlalsMargings(dRoots, rootUpperLimit);
        
        List<Pair<Pair<Number, Number>, Pair<Number, Number>>> searchPairs 
            = getSearchPairs(searchIntervalsMargins, rootUpperLimit, precision);
        final List<Number> newRoots = searchPairs
            .stream()
            .map(pair -> findRootInUpperRectangle(pair, precision, 
                    simpleRootsP))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        final List<Number> rootsWOPossibleDublicates 
            = toRootsWOPossibleDuplicates(newRoots, precision);
        
        final List<Number> newSingleRoots 
                = new ArrayList<>(rootsWOPossibleDublicates);
        newSingleRoots.addAll(getConjugatedRoots(rootsWOPossibleDublicates));
        foundRoots.addAll(newSingleRoots);
        if (foundRoots.size() < coefficients.size() - 1 
                && !rootsWOPossibleDublicates.isEmpty()) {
            Polynomial divisor = Polynomial.fromRoots(newSingleRoots);
            Polynomial newP = simpleRootsP.div(divisor);
            foundRoots.addAll(findRoots(newP, precision));
        }
        return foundRoots;
    }

    /**
     * find root for linear polynomial
     * @param p, polynomial
     * @param precision, precision
     * @return root
     */
    Number findRootForLinear(
            Polynomial p, Number precision
    ) {
        List<Number> coefficients = p.getCoefficients();
        if (coefficients == null) {
            throw new IllegalArgumentException(LINEAR_POLYNOMIAL_IS_EXPECTED);
        }
        int size = coefficients.size();
        if (size != 2) {
            throw new IllegalArgumentException(LINEAR_POLYNOMIAL_IS_EXPECTED
                    + coefficients);
        }
        Polynomial reducedP = p.div(coefficients.get(1)).negative();
        return reducedP.getCoefficients().get(0);
    }

    /**
     * finds root in upper rectangle
     * @param ab, rectangle lower left and upper right corners
     * @param precision, precision
     * @param p, polynomial
     * @return root
     */
    Number findRootInUpperRectangle(
            Pair<Pair<Number, Number>, Pair<Number, Number>> ab,
            Number precision,
            Polynomial p) {
        if (ab == null) {
            throw new IllegalArgumentException(
                    NULL_ARGUMENT_OR_ZERO_PRECISION);
        }
        Pair<Number, Number> a = ab.getFirst();
        Pair<Number, Number> b = ab.getSecond();
        if (a == null || b == null) {
            throw new IllegalArgumentException(
                    NULL_ARGUMENT_OR_ZERO_PRECISION);
        }
        final Number ax = a.getFirst();
        final Number ay = a.getSecond();
        final Number bx = b.getFirst();
        final Number by = b.getSecond();
        if (ax == null || bx == null || ay == null || by == null
                || precision == null
                || ax.doubleValue() == bx.doubleValue()
                || MathHelper.toBigDecimal(precision)
                        .equals(BigDecimal.ZERO)) {
            throw new IllegalArgumentException(
                    NULL_ARGUMENT_OR_ZERO_PRECISION);
        }
        Polynomial p1Type = p.unifyCoefficientsTypes();
        Number multipliedPrecision = p1Type.getMultipliedPrecision(precision);
        if (multipliedPrecision == null) {
            throw new IllegalArgumentException("multipliedPrecision == null");
        }
        if (multipliedPrecision instanceof BigDecimal) {
            Function<BigDecimal, BigDecimal> f
                    = x -> MathHelper.toBigDecimal(p1Type.value(x));
            final BigDecimal axB = MathHelper.toBigDecimal(ax);
            final BigDecimal bxB = MathHelper.toBigDecimal(bx);
            final BigDecimal precisionB
                    = MathHelper.toBigDecimal(multipliedPrecision);
            BigDecimal rootReal = intervalSolver.find(f, axB, bxB, precisionB);

            if (p.isRoot(rootReal, precision)) {
                return rootReal;
            }
            LOG.debug("Real root of {} is not found with precision {}. Iteration is stopped at {}",
                p1Type.getCoefficients(), precision, rootReal);
            final BigDecimal ayB = MathHelper.toBigDecimal(ay);
            final BigDecimal byB = MathHelper.toBigDecimal(by);
            return findComplexRootInUpperRectangle(
                    Pair.of(Pair.of(axB, ayB), Pair.of(bxB, byB)),
                    rootReal,
                    precisionB,
                    p1Type);
        }

        Function<Double, Double> f = x -> (Double) p1Type.value(x);
        final Double precisionD = multipliedPrecision.doubleValue();
        final Double axD = ax.doubleValue();
        final Double bxD = bx.doubleValue();
        Double rootReal = intervalSolver.find(f, axD, bxD, precisionD);

        if (p1Type.isRoot(rootReal, precision)) {
            return rootReal;
        }
        LOG.debug("Real root of {} is not found with precision {}. Iteration is stopped at {}",
                p1Type.getCoefficients(), precision, rootReal);
        final Double ayD = ay.doubleValue();
        final Double byD = by.doubleValue();
        return findComplexRootInUpperRectangle(
                Pair.of(axD, ayD), Pair.of(bxD, byD),
                rootReal,
                precisionD,
                p1Type);
    }

    /**
     * finds complex roots in rectangle 
     * @param ab pair of (x, y) of lower left corner and (x, y) of upper right 
     * corner
     * @param xStart start x-point (real root candidate found previously)
     * @param precision, BigDecimal precision
     * @param p, polynomial
     * @return complex root
     */
    Complex findComplexRootInUpperRectangle(
            Pair<Pair<BigDecimal, BigDecimal>, Pair<BigDecimal, BigDecimal>> ab,
            BigDecimal xStart,
            BigDecimal precision,
            Polynomial p) {
        Pair<BigDecimal, BigDecimal> a = ab.getFirst();
        Pair<BigDecimal, BigDecimal> b = ab.getSecond();
        BigDecimal ax = a.getFirst();
        BigDecimal ay = a.getSecond();
        BigDecimal bx = b.getFirst();
        BigDecimal by = b.getSecond();
        Complex root = new Complex();
        root.setX(xStart);
        root.setY(0.0);
        boolean isY = false;
        int i = 0;
        while (ax.subtract(bx).abs().compareTo(precision) > 0
                && !p.isRoot(root, precision)
                && i++ < IntervalSolver.N_ITERATIONS) {
            isY = !isY;
            if (isY) {
                Number x = root.getX();
                Function<BigDecimal, BigDecimal> f = y -> {
                    Complex arg = new Complex();
                    arg.setX(x);
                    arg.setY(y);
                    Number reVal = ((Complex) p.value(arg)).getX();
                    return (BigDecimal) reVal;
                };
                BigDecimal rootY = intervalSolver.find(f, ay, by, precision);
                Complex newRoot = new Complex();
                newRoot.setX(x);
                newRoot.setY(rootY);
                root = newRoot;
                continue;
            }
            Number y = root.getY();
            Function<BigDecimal, BigDecimal> f = x -> {
                Complex arg = new Complex();
                arg.setX(x);
                arg.setY(y);
                return (BigDecimal) ((Complex) p.value(arg)).getX();
            };
            BigDecimal rootX = intervalSolver.find(f, ax, bx, precision);
            Complex newRoot = new Complex();
            newRoot.setX(rootX);
            newRoot.setY(y);
            root = newRoot;
        }
        if (p.isRoot(root, precision)) {
            return root;
        }

        Complex dRoot = getRootWithDerivativeSolver(p, root, precision);
        return p.isRoot(dRoot, precision) ? dRoot : null;
    }

    /**
     * finds complex roots in rectangle 
     * @param ab pair of (x, y) of lower left corner and (x, y) of upper right 
     * corner
     * @param xStart start x-point (real root candidate found previously)
     * @param precision, Double precision
     * @param p, polynomial
     * @return complex root
     */
    Complex findComplexRootInUpperRectangle(Pair<Double, Double> a,
            Pair<Double, Double> b,
            Double xStart,
            Double precision,
            Polynomial p) {
        double ax = a.getFirst();
        double ay = a.getSecond();
        double bx = b.getFirst();
        double by = b.getSecond();
        Complex root = new Complex();
        root.setX(xStart);
        root.setY(0.0);
        boolean isY = false;
        int i = 0;
        while (MathHelper.abs(ax - bx) > precision
                && !p.isRoot(root, precision)
                && i++ < IntervalSolver.N_ITERATIONS) {
            isY = !isY;
            if (isY) {
                Number x = root.getX();
                Function<Double, Double> f = y -> {
                    Complex arg = new Complex();
                        arg.setX(x);
                        arg.setY(y);
                        return (Double) ((Complex) p.value(arg)).getX();
                };
                Double rootY = intervalSolver.find(f, ay, by, precision);
                Complex newRoot = new Complex();
                newRoot.setX(x);
                newRoot.setY(rootY);
                root = newRoot;
                continue;
            }
            Number y = root.getY();
            Function<Double, Double> f = x -> {
                Complex arg = new Complex();
                arg.setX(x);
                arg.setY(y);
                return (Double) ((Complex) p.value(arg)).getX();
            };
            Double rootX = intervalSolver.find(f, ax, bx, precision);
            Complex newRoot = new Complex();
            newRoot.setX(rootX);
            newRoot.setY(y);
            root = newRoot;
        }
        if (p.isRoot(root, precision)) {
            return root;
        }

        final Function<Complex, Complex> cmplxF = z -> new Complex(p.value(z));
        Polynomial dP = p.derivative();
        final Function<Complex, Complex> cmplxDf
                = z -> new Complex(dP.value(z));
        final BigDecimal precisionB = new BigDecimal(precision);
        Complex dRoot
                = derivativeSolver.find(cmplxF, cmplxDf, root, precisionB);
        return p.isRoot(dRoot, precisionB) ? dRoot : null;
    }

    /**
     * calculates root absolute value upper limit with Cauchy formula
     * @param coefficients, polynomial coefficients
     * @return upper limit
     */
    Double getRootUpperLimitCauchy(List<Number> coefficients) {
        final Deque<Double> coeffsDouble = coefficients
                .stream()
                .filter(Objects::nonNull)
                .map(Number::doubleValue)
                .collect(Collectors.toCollection(ArrayDeque::new));
        Double nCoeff = coeffsDouble.removeLast();
        return 1 + coeffsDouble
                .stream()
                .mapToDouble(c -> Math.abs(c / nCoeff))
                .max()
                .orElse(0);
    }

    /**
     * separates actually multiple roots from close roots
     * @param roots, found root candidates
     * @param p, polynomial to solve
     * @param precision, precision
     * @return actual roots 
     */
    private List<Number> getCloseRootsSeparatedFromMultipleRoots(
            List<Number> roots, Polynomial p, Number precision) {
        List<Number> rootsFromSurroundings
                = getRootsFromSurroundings(roots, p, precision);
        roots.retainAll(rootsFromSurroundings);
        rootsFromSurroundings.removeAll(roots);
        List<Number> foundRoots = new ArrayList<>(roots);
        foundRoots.addAll(new HashSet<>(roots)); // roots are duplicated
        foundRoots.addAll(rootsFromSurroundings);
        return foundRoots;
    }

    /**
     * splits complex rectangle: x in [-rootUpperLimit, rootUpperLimit],
     * y in [0, rootUpperLimit] into smaller rectangles with dRoots
     * @param dRoots, derivative roots
     * @param rootUpperLimit, all roots have abs values less than rootUpperLimit
     * @return 
     */
    private List<Number> getSearchIntevlalsMargings(List<Number> dRoots, 
            Double rootUpperLimit) {
        List<Number> upperDerRootsSorted = dRoots
                .stream()
                .filter(r -> !(r instanceof Complex)
                        || MathHelper.toBigDecimal(new Complex(r).getY())
                                .compareTo(BigDecimal.ZERO) >= 0)
                .map(r -> r instanceof Complex ? r : MathHelper.toBigDecimal(r))
                .sorted(getComparatorForDifferentTypes())
                .collect(Collectors.toList());
        List<Number> searchIntervalsMargins = new ArrayList<>();
        searchIntervalsMargins.add(-rootUpperLimit);
        searchIntervalsMargins.addAll(upperDerRootsSorted);
        searchIntervalsMargins.add(rootUpperLimit);
        return searchIntervalsMargins;
    }

    /**
     * Builds search pairs from search intervals margins
     * @param searchIntervalsMargins
     * @param rootUpperLimit
     * @param precision
     * @return search pairs, from (x, y) to (x, y),
     * close margins are unified
     */
    private List<Pair<Pair<Number, Number>, 
        Pair<Number, Number>>> getSearchPairs(
                List<Number> searchIntervalsMargins, 
                Double rootUpperLimit, 
                Number precision
        ) {
        final int nIntervals = searchIntervalsMargins.size();
        final List<Pair<Pair<Number, Number>, Pair<Number, Number>>> searchPairs
                = new ArrayList<>();
        Pair<Number, Number> p1 = Pair.of(-rootUpperLimit, 0);
        for (int i = 1; i < nIntervals; i++) {
            Number m1 = searchIntervalsMargins.get(i);
            
            Complex m1C = new Complex(m1);
            Pair<Number, Number> p2;
            if (m1 instanceof Complex) {
                p2 = Pair.of(m1C.getX(), m1C.getY());
                searchPairs.add(Pair.of(p1, p2));
                Complex precisionI = new Complex(0, precision);
                p1 = Pair.of(p1.getFirst(), m1C.plus(precisionI).getY());
                
                if (i == nIntervals - 1) {
                    p2 = Pair.of(m1C.getX(), rootUpperLimit);
                    searchPairs.add(Pair.of(p1, p2));
                    p1 = Pair.of(m1C.plus(precision).getX(), 0);
                    continue;
                }
                
                Number m2 = searchIntervalsMargins.get(i + 1);
                Complex m2C = new Complex(m2);
                boolean areMarginsClose = m2C.minus(m1C).compareTo(precision) 
                        <= 0; // here margins are considered ordered
                if (m2 instanceof Complex && areMarginsClose) { 
                    p2 = Pair.of(m1C.getX(), m2C.getY());
                    searchPairs.add(Pair.of(p1, p2));
                    p1 = Pair.of(p1.getFirst(), m2C.plus(precisionI).getY());
                } else {
                    p2 = Pair.of(m1C.getX(), rootUpperLimit);
                    searchPairs.add(Pair.of(p1, p2));
                    p1 = Pair.of(m1C.plus(precision).getX(), 0);
                    if (areMarginsClose) {
                        i++;
                    }
                }
            } else {
                p2 = Pair.of(m1, rootUpperLimit);
                searchPairs.add(Pair.of(p1, p2));
                p1 = Pair.of(m1C.plus(precision).getX(), 0);
            }
            
        }
        return searchPairs;
    }

    /**
     * adds to found roots their conjugated pairs
     * @param foundRoots, found roots
     * @return complete found roots
     */
    private List<Complex>  getConjugatedRoots(List<Number> foundRoots) {
        return foundRoots
            .stream()
            .map(Complex::new)
            .filter(r -> MathHelper.toBigDecimal(r.getY())
                    .compareTo(BigDecimal.ZERO) != 0)
            .map(Complex::conjugate)
            .collect(Collectors.toList());
    }
    
    /**
     * find roots in surroundings of root candidates 
     * (used for separation derivative roots detected as this polynomial (p) 
     * roots from polynomial roots in close roots case)
     * @param rootCandidates, root candidates 
     * @param p, polynomial
     * @param precision, precision
     * @return multiple roots and roots from surroundings
     */
    private List<Number> getRootsFromSurroundings(List<Number> rootCandidates, 
            Polynomial p, Number precision) {
        List<Pair<Number, List<Complex>>> rootCandidateToGuesses 
            = rootCandidates.stream()
                .map(r -> Pair.of(r, getGuessesAtPoint(r, p, precision)))
                .collect(Collectors.toList());
        List<Number> newRoots = rootCandidateToGuesses.stream()
            .flatMap(pair -> {
                List<Complex> guesses = pair.getSecond();
                if (guesses.isEmpty()) {
                    return Stream.of(pair.getFirst());
                }
                return guesses.stream()
                    .map(g -> getRootWithDerivativeSolver(p, g, precision))
                    .filter(r -> p.isRoot(r, precision))
                    .map(Complex::toRealIfPossible);
            })
            .collect(Collectors.toList());
        List<Number> foundRoots = new ArrayList<>(newRoots);
        List<Number> rootsWOPossibleDublicates 
            = toRootsWOPossibleDuplicates(foundRoots, precision);
        return new ArrayList<>(rootsWOPossibleDublicates);
    }
    
    /**
     * gets guesses at derivative root for given polynomial (p) roots 
     * search
     * @param point, derivative root 
     * @param p, polynomial
     * @param precision, precision
     * @return guesses for roots search
     */
    private List<Complex> getGuessesAtPoint(Number point, Polynomial p, 
            Number precision) {
        BigDecimal precisionB = MathHelper.toBigDecimal(precision);
        BigDecimal deflection = precisionB
                .multiply(new BigDecimal(DEFLECTION_FACTOR_FOR_CLOSE_ROOTS));
        List<Complex> rGuesses = new ArrayList<>();
        
        BigDecimal value
            = MathHelper.toBigDecimal(new Complex(p.value(point)).getX()).abs();

        Complex rRight = new Complex(point).plus(deflection);
        BigDecimal valueRight
                = MathHelper.toBigDecimal(new Complex(p.value(rRight)).getX())
                        .abs();

        Complex rLeft = new Complex(point).minus(deflection);
        BigDecimal valueLeft
                = MathHelper.toBigDecimal(new Complex(p.value(rLeft)).getX())
                        .abs();

        if (value.compareTo(valueRight) >= 0
                && value.compareTo(valueLeft) >= 0) {
            rGuesses.add(rRight);
            rGuesses.add(rLeft);
        }

        Complex deflectionI = new Complex(0, deflection);
        Complex rUpper = new Complex(point).plus(deflectionI);
        BigDecimal valueUpper
                = MathHelper.toBigDecimal(new Complex(p.value(rUpper)).getX())
                        .abs();

        Complex rLower = new Complex(point).minus(deflectionI);
        BigDecimal valueLower
                = MathHelper.toBigDecimal(new Complex(p.value(rLower)).getX())
                        .abs();
        if (value.compareTo(valueLower) >= 0
                && value.compareTo(valueUpper) >= 0) {
            rGuesses.add(rLower);
            rGuesses.add(rUpper);
        }
        return rGuesses;
    }
    
    /**
     * prepares parameters and finds roots with derivative solver
     * @param p, polynomial
     * @param guess, guess point to search for root
     * @param precision, precision
     * @return root found
     */
    private Complex getRootWithDerivativeSolver(Polynomial p, Complex guess, 
            Number precision) {
        final BigDecimal precisionB = MathHelper.toBigDecimal(precision);
        final Function<Complex, Complex> cmplxF = z -> new Complex(p.value(z));
        Polynomial dP = p.derivative();
        final Function<Complex, Complex> cmplxDf
                = z -> new Complex(dP.value(z));
        return derivativeSolver.find(cmplxF, cmplxDf, guess, precisionB);
    }

    /**
     * removes possible duplicates 
     * @param, roots
     * @param precision, precision
     * @return rectified roots
     */
    private List<Number> toRootsWOPossibleDuplicates(List<Number> roots, 
            Number precision) {
        List<Number> rootsSorted = roots
            .stream()
            .sorted(getComparatorForDifferentTypes())
            .collect(Collectors.toList());
        final int size = rootsSorted.size();
        return IntStream.range(0, size).filter(i -> IntStream.range(i + 1, size)
                .noneMatch(j -> areNumbersClose(roots.get(i), roots.get(j), precision)))
                .mapToObj(i -> roots.get(i))
        .collect(Collectors.toList());
    }

    /**
     * builds a comparator to compare Number with Complex
     * @return comparator
     */
    private Comparator<Number> getComparatorForDifferentTypes() {
        return (Number o1, Number o2) 
                -> new Complex(o1).compareTo(new Complex(o2));
    }
    
    /**
     * checks if numbers with given precision
     * @param n1, number 1
     * @param n2, number 2
     * @param precision, precision
     * @return true if numbers are close, false otherwise
     */
    private boolean areNumbersClose(Number n1, Number n2, Number precision) {
        Complex r1 = new Complex(n1);
        Complex r2 = new Complex(n2);
        Comparable delta2 = MathHelper.toBigDecimal(r1.minus(r2).abs2());
        Comparable precision2 = MathHelper.toBigDecimal(new Complex(precision)
                .multiply(ACCEPTABLE_DEFLECTION_PRECISION_FACTOR)
                .abs2());
        return delta2.compareTo(precision2) <= 0;
    }
}
