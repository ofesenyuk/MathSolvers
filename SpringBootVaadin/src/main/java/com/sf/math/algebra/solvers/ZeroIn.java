/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.math.algebra.solvers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;
import org.springframework.stereotype.Service;

/**
 *
 * @author sf
 */
@Service
public class ZeroIn implements IntervalSolver {


    private double a, b, c, d, e, xm, tol1  = 1.0;

    private double fa, fb, fc;

    private BigDecimal aB, bB, cB, dB, eB, xmB, tol1B  = BigDecimal.ONE;

    private BigDecimal faB, fbB, fcB;

    /**
     * Zero of a function on an interval.When there is no zero on the interval,
     * returns the argument of the minimum of the absolute value of the
     * function.
     *
     * @param <T>
     * @param f function
     * @param ax from
     * @param bx to
     * @param eps precision
     * @return root found
     */
    @Override
    public Double find(final Function<Double, Double> f,
            final double ax, final double bx, final double eps) {
        double p, q, r, s;

        a = ax;
        b = bx;
        fa = f.apply(a);
        fb = f.apply(b);
        centralAssignments();
        while(true) {
            if (Math.abs(fc) < Math.abs(fb)) {
                a = b;
                b = c;
                c = a;
                fa = fb;
                fb = fc;
                fc = fa;
            }
            tol1 = 2.0 * eps * Math.abs(b) + 0.5 * eps;
            xm = 0.5 * (c - b);
            if (Math.abs(xm) <= tol1 || fb == 0.0) {
                return b;
            }
            if (Math.abs(e) < tol1 || Math.abs(fa) <= Math.abs(fb)) {
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
            if (p > 0.0) {
                q = -q;
            }
            p = Math.abs(p);
            if ((2.0 * p) >= (3.0 * xm * q - Math.abs(tol1 * q))
                    || p >= Math.abs(0.5 * e * q)) {
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

    @Override
    public BigDecimal find(Function<BigDecimal, BigDecimal> f, 
            BigDecimal ax, BigDecimal bx, BigDecimal eps) {
        int scale = eps.abs().scale();
        BigDecimal p, q, r, s;

        aB = ax;
        bB = bx;
        faB = f.apply(aB);
        fbB = f.apply(bB);
        centralAssignmentsB();
        while (true) {
            if (fcB.abs().compareTo(fbB.abs()) < 0) {
                aB = bB;
                bB = cB;
                cB = aB;
                faB = fbB;
                fbB = fcB;
                fcB = faB;
            }
            tol1B = TWO.multiply(eps).multiply(bB.abs()).add(eps.divide(TWO));
            xmB = cB.subtract(bB).divide(TWO);
            if (xmB.abs().compareTo(tol1B) <= 0 
                    || fbB.equals(BigDecimal.ZERO)) {
                return bB;
            }
            if (eB.abs().compareTo(tol1B) < 0 
                    || faB.abs().compareTo(fbB) <= 0) {
                dB = xmB;
                eB = dB;
                finalIterationAssignmentsB(f);
                continue;
            }
            if (aB.equals(cB)) {
                s = fbB.divide(faB, scale, RoundingMode.CEILING);
                p = TWO.multiply(xmB).multiply(s);
                q = BigDecimal.ONE.subtract(s);
            } else {
                q = faB.divide(fcB, scale, RoundingMode.CEILING);
                r = fbB.divide(fcB, scale, RoundingMode.CEILING);
                s = fbB.divide(faB, scale, RoundingMode.CEILING);
                p = s.multiply(TWO.multiply(xmB).multiply(q)
                        .multiply(q.subtract(r))
                    .subtract((bB.subtract(aB))
                            .multiply(r.subtract(BigDecimal.ONE))));
                q = q.subtract(BigDecimal.ONE)
                        .multiply(r.subtract(BigDecimal.ONE))
                        .multiply(s.subtract(BigDecimal.ONE));
            }
            if (p.compareTo(BigDecimal.ZERO) > 0) {
                q = q.negate();
            }
            p = p.abs();
            if (TWO.multiply(p)
                    .compareTo(THREE.multiply(xmB).multiply(q)
                            .subtract(tol1B.multiply(q).abs())) >= 0
                    || p.compareTo(eB.multiply(q).divide(TWO).abs()) >= 0) {
                dB = xmB;
                eB = dB;
                finalIterationAssignmentsB(f);
                continue;
            }
            eB = dB;
            dB = p.divide(q, scale, RoundingMode.CEILING);
            finalIterationAssignmentsB(f);
        }
    }

    private void centralAssignments() {
        c = a;
        fc = fa;
        d = b - a;
        e = d;
    }

    private void centralAssignmentsB() {
        cB = aB;
        fcB = faB;
        dB = bB.subtract(aB);
        eB = dB;
    }

    private void finalIterationAssignments(final Function<Double, Double> f) {
        a = b;
        fa = fb;
        if (Math.abs(d) > tol1) {
            b = b + d;
        } else {
            b = b + sign(tol1, xm);
        }
        fb = f.apply(b);
        if (fb * (fc / Math.abs(fc)) > 0.0) {
            centralAssignments();
        }
    }

    private void finalIterationAssignmentsB(final Function<BigDecimal, 
            BigDecimal> f) {
        aB = bB;
        faB = fbB;
        if (dB.abs().compareTo(tol1B) > 0) {
            bB = bB.add(dB);
        } else {
            bB = bB.add(sign(tol1B, xmB));
        }
        fbB = f.apply(bB);
//        if (fb * (fc / Math.abs(fc)) > 0.0) {
        if (fbB.signum() * fcB.signum() > 0) {
            centralAssignmentsB();
        }
    }

    private double sign(double tol1, double xm) {
        return xm >= 0.0 ? Math.abs(tol1) : -Math.abs(tol1);
    }

    private BigDecimal sign(BigDecimal tol1B, BigDecimal xmB) {
        return xmB.compareTo(BigDecimal.ZERO) >= 0 
                ? tol1B.abs() 
                : tol1B.abs().negate();
    }

}
