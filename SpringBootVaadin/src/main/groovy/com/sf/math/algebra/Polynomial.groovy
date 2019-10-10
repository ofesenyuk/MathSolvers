/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sf.math.algebra

import com.sf.math.number.Complex
import java.util.stream.Collectors;

/**
 *
 * @author sf
 */
class Polynomial {
    List<Number> coefficients;
    
    Polynomial(List<Number> coefficients) {
        if (coefficients) {            
            this.coefficients = keepNotNullTail(coefficients);
        }
    }
    
    static Polynomial fromRoots(List<Number> roots) {
        if (!roots) {
            return new Polynomial(null);
        }
        
        Polynomial result = new Polynomial([1]);
        for (int i = 0; i < roots.size(); i++) {
            result = new Polynomial([-roots[i], 1]) * result;
            //            println 'result ' + result.coefficients + ' i ' + i + ' ' + new Polynomial([-roots[i], 1]).coefficients;
        }
        return result;
    } 
    
    Number value(Number x) {
        if (!coefficients) {
            return 0;
        }
        
        Number result = coefficients.last();   
        for (int i = coefficients.size() - 2; i >= 0; i--) {
            result = coefficients[i]  + (x instanceof Complex ? x * result : result * x);
        }
        return result;
    }
        
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
        
    Polynomial minus(Polynomial p2) {
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
            newCoeff -= c2;
        };
        return new Polynomial(newCoeffs); 
    }
    
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
    
    private List<Number> keepNotNullTail(List<Number> list) {
        LinkedList notNullTail = new LinkedList();
        for (int i = list.size() - 1; i >= 0; i--) {
            Number e = list[i] ?: 0;
            if (notNullTail.isEmpty() && !e) {
                continue;
            }
            notNullTail.addFirst(e);
        }
        return new ArrayList(notNullTail);
    }
    
    private boolean areEmptyCoefficientsPresent(Polynomial p2) {
        coefficients == null || coefficients.isEmpty() || p2.coefficients == null || p2.coefficients.isEmpty();
    }
    
    private Polynomial shiftToPower(Polynomial p, int power) {
        if (power <= p.coefficients.size() - 1) {
            return new Polynomial(p.coefficients);
        }
        List<Number> newCoeffs = [];
        def zeroRange = p.coefficients.size()..power;
        zeroRange.each{newCoeffs << 0};
        newCoeffs.addAll(p.coefficients);
        return new Polynomial(newCoeffs);
    }
}
