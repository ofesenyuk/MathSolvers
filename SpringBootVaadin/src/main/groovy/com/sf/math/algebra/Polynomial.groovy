/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sf.math.algebra

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
        
    }    
        
    Polynomial plus(Number op) {
        if (coefficients && !coefficients.isEmpty()) {
            List<Number> newCoeff = coefficients.stream()
                .collect(Collectors.toList());
            newCoeff[0] += op ?: 0;
            return new Polynomial(newCoeff);
        }
        return new Polynomial([op]); 
    }
        
    Polynomial plus(Polynomial p2) {
        if (areEmptyCoefficientsPresent(p2)) {
                throw new NullPointerException("at least, one of coefficients is empty");
        }
        
        int nMax = Math.max(coefficients.size(), p2.coefficients.size());        
        LinkedList newCoeffs = new LinkedList();
        for (int i = nMax - 1; i >=0; i--) {
            Number newCoeff = i < coefficients.size() ? coefficients[i] : 0;
            newCoeff += i < p2.coefficients.size() ? p2.coefficients[i] : 0;
            
            if (newCoeffs.isEmpty() && newCoeff == 0) {
                continue;
            }
            newCoeffs.addFirst(newCoeff);
        }
        return new Polynomial(newCoeffs); 
    }
        
    Polynomial minus(Number op) {
        if (coefficients && !coefficients.isEmpty()) {
            List<Number> newCoeff = coefficients.stream()
                .collect(Collectors.toList());
            newCoeff[0] -= op ?: 0;
            return new Polynomial(newCoeff);
        }
        return new Polynomial([op]); 
    }
        
    Polynomial minus(Polynomial p2) {
        if (areEmptyCoefficientsPresent(p2)) {
                throw new NullPointerException("at least, one of coefficients is empty");
        }
        
        int nMax = Math.max(coefficients.size(), p2.coefficients.size());        
        LinkedList newCoeffs = new LinkedList();
        for (int i = nMax - 1; i >=0; i--) {
            Number newCoeff = i < coefficients.size() ? coefficients[i] : 0;
            newCoeff -= i < p2.coefficients.size() ? p2.coefficients[i] : 0;
            
            if (newCoeffs.isEmpty() && newCoeff == 0) {
                continue;
            }
            newCoeffs.addFirst(newCoeff);
        }
        return new Polynomial(newCoeffs); 
    }
    
    Polynomial multiply(Number op) {
        return new Polynomial(coefficients.collect{c -> c * op});
    }
    
    Polynomial multiply(Polynomial op) {
        return op;
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
}
