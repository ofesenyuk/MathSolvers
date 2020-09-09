/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sf.math.algebra

import java.util.stream.Collectors;
import java.math.RoundingMode;
import com.helger.commons.math.MathHelper;

import com.sf.math.number.Complex

/**
 *
 * @author sf
 */
class Polynomial {
    Number precisionFactor = 1;
    List<Number> coefficients;
    
    /**
     * Constructor for creation a polynomial from given coefficients     * 
     */
    Polynomial(List<Number> coefficients) {
        if (coefficients) {            
            this.coefficients = keepNotNullTail(coefficients);
        } else {
            this.coefficients = [1];
        }
        this.precisionFactor = getPrecisionFactor();
    }
    
    /**
     * Creates a polynomial from its roots
     */ 
    static Polynomial fromRoots(List<Number> roots) {
        if (!roots) {
            return new Polynomial(null);
        }
        
        Polynomial result = new Polynomial([1]);
        for (int i = 0; i < roots.size(); i++) {
            result = new Polynomial([-roots[i], 1]) * result;
        }
        return result.toRealCoefficientIfPossible();
    } 
    
    /**
     * Converts coefficients to real (Double or BigDecimal) type is their 
     * imaginary parts are zeroes.
     */ 
    Polynomial toRealCoefficientIfPossible() {
        List<Number> newCoeffs = this.coefficients.collect{
            if (it instanceof Complex && it.isReal()) {
                return it.x;
            }
            it;
        };
        new Polynomial(newCoeffs);
    }
    
    /**
     * Calculates value of polynomial of x argument
     */
    Number value(Number x) {
        if (!coefficients) {
            return 0;
        }
        
        Number result = coefficients.last();
        for (int i = coefficients.size() - 2; i >= 0; i--) {
            Number coeff = coefficients[i];
            if (x instanceof Complex) {
                if (x.x instanceof BigDecimal || x.y instanceof BigDecimal) {
                    Complex xC = new Complex(x).toComplexBigDecimal();
                    Complex resultC = new Complex(result).toComplexBigDecimal();
                    Complex coeffC = new Complex(coeff).toComplexBigDecimal();
                    result = xC.multiply(resultC).plus(coeffC);
                } else {
                    result = x * result + coeff;
                }
            } else {
                result = coeff  + result * x;
            }
        }
        return result;
    }    
    
    /**
     * Calculates value of polynomial of x argument
     */
    Number value(BigDecimal x) {
        if (!coefficients) {
            return 0;
        }
        
        Number last = coefficients.last();
        Complex cLast = new Complex(last);
        Number result = cLast.isReal() 
            ? MathHelper.toBigDecimal(cLast.x) : last;
        for (int i = coefficients.size() - 2; i >= 0; i--) {
            Number coeff = MathHelper.toBigDecimal(coefficients[i]);
            result = coeff  + result * x;   
        }
        return result;
    }
        
    /**
     * Overrides '+' operator for first operand of Polynomial type, 
     * second operand of Number type
     */
    Polynomial plus(Number op) {
        if (coefficients && !coefficients.isEmpty()) {
            List<Number> newCoeff = coefficients.stream()
            .collect(Collectors.toList());
            if (op) {
                if (op instanceof Complex) {
                    newCoeff[0] = new Complex(x: newCoeff[0], 0);
                }
                newCoeff[0] += op;
            }
            return new Polynomial(newCoeff);
        }
        return new Polynomial([op]); 
    }
        
    /**
     * Overrides '+' operator for first operand of Polynomial type, 
     * second operand of Polynomial type
     */
    Polynomial plus(Polynomial p2) {
        if (areEmptyCoefficientsPresent(p2)) {
            throw new NullPointerException("at least, one of coefficients is empty");
        }
        
        int nMax = Math.max(coefficients.size(), p2.coefficients.size()); 
        List<Number> newCoeffs = (0..nMax).collect{i -> 
            Number newCoeff = i < coefficients.size() ? coefficients[i] : 0;
            Number c2 = i < p2.coefficients.size() ? p2.coefficients[i] : 0;
            if (c2 instanceof Complex && !(newCoeff instanceof Complex)) {
                newCoeff = new Complex(x: newCoeff, 0);
            }
            newCoeff += c2;
        };
        return new Polynomial(newCoeffs); 
    }    
    
    /**
     * Overrides '-' operator for first operand of Polynomial type, 
     * second operand of Number type
     */
    Polynomial minus(Number op) {
        if (coefficients && !coefficients.isEmpty()) {
            List<Number> newCoeff = coefficients.stream()
            .collect(Collectors.toList());
            if (op) {
                if (op instanceof Complex) {
                    newCoeff[0] = new Complex(x: newCoeff[0], 0);
                }
                newCoeff[0] -= op;
            }
            return new Polynomial(newCoeff);
        }
        return new Polynomial([op]); 
    }
       
    /**
     * Overrides '-' operator for first operand of Polynomial type, 
     * second operand of Polynomial type
     */
    Polynomial minus(Polynomial p2) {
        if (areEmptyCoefficientsPresent(p2)) {
            throw new NullPointerException("at least, one of coefficients is empty");
        }
        
        int nMax = Math.max(coefficients.size(), p2.coefficients.size());        
        List<Number> newCoeffs = (0..nMax).collect{i -> 
            Number newCoeff = i < coefficients.size() ? coefficients[i] : 0;
            Number c2 = i < p2.coefficients.size() ? p2.coefficients[i] : 0;
            if (c2 instanceof Complex && !(newCoeff instanceof Complex)) {
                newCoeff = new Complex(newCoeff, 0);
            }
            newCoeff -= c2;
            return newCoeff;
        };
        return new Polynomial(newCoeffs); 
    }
    
    /**
     * Overrides '*' operator for first operand of Polynomial type, 
     * second operand of Number type
     */
    Polynomial multiply(Number op) {
        return new Polynomial(coefficients.collect{c -> 
                if (!c || !op) {
                    return 0;
                }
                if (op instanceof Complex) {
                    return op * c;
                }
                return c * op;
            });
    }
    
    /**
     * Overrides '*' operator for first operand of Polynomial type, 
     * second operand of Polynomial type
     */
    Polynomial multiply(Polynomial op) {
        Map<Integer,Number> powerToCoeff = [:];
        def thisRange = 0..<coefficients.size();
        def opRange = 0..<op.coefficients.size();
        thisRange.each{i -> 
            Number a = coefficients[i]?:0;
            opRange.each{j -> 
                Integer powNew = i + j;
                Number b = op.coefficients[j]?:0;
                Number factor2 = (a instanceof Complex) ? a * b : b * a;
                Number factor1 = powerToCoeff.getOrDefault(powNew, 0);
                Number res = (factor2 instanceof Complex) ? factor2 + factor1 : factor1 + factor2;
                powerToCoeff.put(powNew, res);      
            }
        }
        return new Polynomial(new ArrayList(powerToCoeff.values()));
    }
    
    /**
     * Overrides '/' operator for first operand of Polynomial type, 
     * second operand of Number type
     */
    Polynomial div(Number op) {
        return new Polynomial(coefficients.collect{c -> 
                if (!c) {
                    return 0;
                }
                if (op instanceof Complex && !(c instanceof Complex)) {
                    return new Complex(x: c, y: 0) / op;
                }
                return c / op;
        });
    }
    
    /**
     * Overrides '/' operator for first operand of Polynomial type, 
     * second operand of Polynomial type
     */
    Polynomial div(Polynomial op) {
        if (coefficients.size() < op.coefficients.size()) {
            return new Polynomial([0]);
        }
        Polynomial floatOp = new Polynomial(op.coefficients.collect{
            if (it instanceof Integer || it instanceof Long) {
                return it.doubleValue();
            }
            if (it instanceof BigInteger) {
                return new BigDecimal(it);
            }
            return it;
        });
        List<Number> newCoeffs = [];
        def range = (coefficients.size() - 1)..(op.coefficients.size() - 1);
        Polynomial res = this;
        range.each{
            Number c = res.coefficients[it] / floatOp.coefficients.last();
            newCoeffs << c;
            res -= (shiftToPower(floatOp, it) * res.coefficients[it]) / floatOp.coefficients.last(); 
        };
        return new Polynomial(newCoeffs.reverse());
    }
    
    /**
     * Overrides '-' unary operator 
     */
    Polynomial negative() {
        return new Polynomial(coefficients?.collect{c -> -c});
    }
    
    /**
     * Returns derivative of this polynomial
     */ 
    Polynomial derivative() {
        if (!coefficients || coefficients.size() == 1) {
            return new Polynomial([0]);
        }
        def range = 1..(coefficients.size() - 1);
        List<Number> dCoefficients = range.collect{n -> n * coefficients[n]};
        return new Polynomial(dCoefficients)
    }
    
    /**
     * Checks if root is root with given precision
     */
    Boolean isRoot(Number root, Number precision) {
        Number val;
        if (precision instanceof BigDecimal) {
            if (root instanceof Complex) {
                val = this.value(new Complex(MathHelper.toBigDecimal(root.x), 
                        MathHelper.toBigDecimal(root.y))) / MathHelper.toBigDecimal(precisionFactor);
            } else {
                val = this.value(MathHelper.toBigDecimal(root)) / MathHelper.toBigDecimal(precisionFactor);
            }
        } else {
            val = this.value(root) / precisionFactor;
        }
        Boolean cmp = compareAbs(val, precision) <= 0;
        return cmp;
    }
    
    /**
     * returns polynomial with coefficients of single type
     */
    Polynomial unifyCoefficientsTypes() {
        Boolean isComplex = this.coefficients.any{it && it instanceof Complex}
        if (isComplex) {
            return this.toComplex();
        }
        if (isBigDecimalPresent(this.coefficients)) {
            return new Polynomial(coefficients.collect{it != null && !(it instanceof BigDecimal) ? new BigDecimal(it.toString()) : it});
        };
        
        new Polynomial(coefficients.collect{it != null ? it.doubleValue() : it});
    }
    
    /**
     * returns polynomial with coefficients of Complex type
     */
    Polynomial toComplex() {
        Boolean isBigDecimal = isBigDecimalPresent(this.coefficients);
        List<Number> newCoeffs = coefficients.collect{c ->
            if (c != null && !(c instanceof Complex)) {
                Number x = !(c instanceof BigDecimal) && isBigDecimal ? new BigDecimal(c.toString()) : c; 
                Number y = isBigDecimal ? BigDecimal.ZERO : 0.0;
                new Complex(x, y);
            } else {
                c;
            }
        };
        new Polynomial(newCoeffs);
    }
    
    /**
     * returns precision divided by precisionFactor
     */
    Number getMultipliedPrecision(Number precision) {
        precision instanceof BigDecimal 
            ? precision.divide(precisionFactor, RoundingMode.CEILING) 
            : precision.div(precisionFactor);
    }
    
    /**
     * checks if any coefficient is of type BigDecimal
     */
    private boolean isBigDecimalPresent(Collection<Number> list) {
        list?.any{it != null && it instanceof BigDecimal};
    }
    
    /**
     * keep first highest-order not null, not zero coefficient 
     * and all lower-order coefficients
     */
    private List<Number> keepNotNullTail(List<Number> list) {
        LinkedList notNullTail = new LinkedList();
        for (int i = list.size() - 1; i >= 0; i--) {
            Number e = list[i] != null ? list[i] : 0;
            if (!notNullTail.isEmpty()) {
                notNullTail.addFirst(e);
                continue;
            }
            if (e instanceof Complex && e.isZero()) {
                continue;
            }
            if (!(e instanceof Complex) && e.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            if (!e && !(e instanceof Complex)) {
                continue;
            }
            notNullTail.addFirst(e);
        }
        return new ArrayList(notNullTail);
    }
    
    /**
     * checks if any of operands-polynomials have empty coefficients list
     */
    private boolean areEmptyCoefficientsPresent(Polynomial p2) {
        coefficients == null || coefficients.isEmpty() || p2.coefficients == null || p2.coefficients.isEmpty();
    }
    
    /**
     * Multiplies polynomial p with x^power if power < size of p
     */
    private Polynomial shiftToPower(Polynomial p, int power) {
        int size = p.coefficients.size();
        if (power < size) {
            return new Polynomial(p.coefficients);
        }
        List<Number> newCoeffs = [];
        def zeroRange = size..power;
        zeroRange.each{newCoeffs << 0};
        newCoeffs.addAll(p.coefficients);
        return new Polynomial(newCoeffs);
    }
    
    /**
     * Compares absolute value of Complex val with precision 
     */
    private int compareAbs(Complex val, BigDecimal precision) {
        def val2 = val.abs2();
        def retVal = val.abs2().compareTo(precision.pow(2));
        return retVal;
    }
    
//    private int compareAbs(Complex val, Double precision) {
//        Math.sqrt(val.abs2().doubleValue()).compareTo(precision);
//    }
    
    
    /**
     * Compares absolute value of Complex val with precision 
     */
    /*
     * never called, with 'BigDecimal precision' or with 'Double precision' is called
    */
    private int compareAbs(Complex val, Number precision) {
        val.abs2().doubleValue().compareTo(Math.pow(precision.doubleValue(), 2));
    }
    
    
    /**
     * Compares absolute value of Number val with precision 
     */
    private int compareAbs(Number val, Number precision) {
        MathHelper.abs(val).compareTo(precision);
    }
    
    /**
     * calculates precision factor in in assumption abs(root) << precision
     * Precision factor is  deflection polynomial value from 0 divided by 
     * precision
     */
    private Number getPrecisionFactor() {
        Number factor = 1;
        if (!coefficients) {
            return factor;
        }
        (0..(coefficients.size() - 1)).each{
            Number coeff = coefficients[it];
            if (!coeff) {
                return;
            }
            Number sumAbs = coeff instanceof Complex 
                ? MathHelper.abs(coeff.x) + MathHelper.abs(coeff.y) 
                : MathHelper.abs(coeff);
            factor += Math.max(sumAbs.doubleValue(), 1.0);
        };
        return factor;
    }
}
