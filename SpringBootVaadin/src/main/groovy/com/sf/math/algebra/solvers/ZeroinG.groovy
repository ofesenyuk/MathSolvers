/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sf.math.algebra.solvers

import groovy.transform.Canonical;

/**
 *
 * @author OFeseniuk
 */
@Canonical
class ZeroinG {
    def a, b, c, d, e, xm, tol1  = 1;
    
    def fa, fb, fc;
    
    def find(final Closure f, def ax, def bx, def eps) {
        def p, q, r, s;
        
//        println "ZeroinG before fa ax = $ax "+ ax.getClass();
        a = ax;
        b = bx;
        fa = f.call(a);
        fb = f.call(b);
        centralAssignments();
//        println "ZeroinG before while";
        while(true) {
            if (abs(fc).compareTo(abs(fb)) < 0) {
                a = b;
                b = c;
                c = a;
                fa = fb;
                fb = fc;
                fc = fa;
            }
            tol1 = 2.0 * eps * abs(b) + 0.5 * eps;
            xm = 0.5 * (c - b);
            if (abs(xm).compareTo(tol1) <= 0 || isZero(fb)) {
                return b;
            }
            if (abs(e).compareTo(tol1) < 0 || abs(fa).compareTo(abs(fb)) <= 0) {
                d = xm;
                e = d;
                finalIterationAssignments(f);
                continue;
            }
            if (a == b) {
                s = fb / fa;
                p = 2.0 * xm * s;
                q = 1.0 - s;
            } else {
                q = fa / fc;
                r = fb / fc;
                s = fb / fa;
                p = s * (2.0 * xm * q * (q - r) - (b - a) * (r - 1.0));
                q = (q - 1.0) * (r - 1.0) * (s - 1.0);
            }
            if (isPositive(p)) {
                q = -q;
            }
            p = abs(p);
            if ((2.0 * p).compareTo(3.0 * xm * q - abs(tol1 * q)) >= 0
                || p.compareTo(abs(0.5 * e * q)) >= 0) {
                d = xm;
                e = d;
                finalIterationAssignments(f);
                continue;
            }
            e = d;
            d = p / q;
            finalIterationAssignments(f);
        }
    }    
    
    private void finalIterationAssignments(final Closure f) {
        a = b;
        fa = fb;
        if (abs(d).compareTo(tol1) > 0) {
            b = b + d;
        } else {
            b = b + sign(tol1, xm);
        }
//        println "finalIterationAssignments b = $b" + b.getClass();
        fb = f(b);
        if (isPositive(fb * (fc / abs(fc)))) {
            centralAssignments();
        }
    }
    
    private void centralAssignments() {
        c = a;
        fc = fa;
        d = b - a;
        e = d;
    }
    
    private double sign(double tol1, double xm) {
        return xm >= 0.0 ? Math.abs(tol1) : -Math.abs(tol1);
    }
    
    private BigDecimal sign(BigDecimal tol1B, BigDecimal xmB) {
        return xmB.compareTo(BigDecimal.ZERO) >= 0 
        ? tol1B.abs() 
    : tol1B.abs().negate();
    }
    
    private Double abs(Double x) {
        return Math.abs(x);
    }
    
    private BigDecimal abs(BigDecimal x) {
        return x.abs();
    }
    
    private Boolean isPositive(Double x) {
        return x > 0.0;
    }
    
    private Boolean isPositive(BigDecimal x) {
        return x.compareTo(BigDecimal.ZERO) > 0;
    }
    
    private Boolean isZero(Double x) {
        return x == 0.0;
    }
    
    private Boolean isZero(BigDecimal x) {
        return x.compareTo(BigDecimal.ZERO) == 0;
    }
}

