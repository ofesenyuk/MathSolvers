/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sf.math.number;

import com.helger.commons.math.MathHelper;

/**
 *
 * @author sf
 */
class Complex extends Number implements Comparable {
    Number x;
    Number y;
    
    Complex() {
        
    }
    
    Complex(Complex c) {
        this.x = c.x;
        this.y = c.y;
    }
    
    Complex(BigDecimal b) {
        this.x = b;
        this.y = BigDecimal.ZERO;
    }
    
    Complex(Number n) {
        if (n instanceof Complex) {
            this.x = n.x;
            this.y = n.y;
            return;
        }
        this.x = n;
        this.y = 0;
    }
    
    Complex(Number x, Number y) {
        this.x = x;
        this.y = y;
    }
    
    float floatValue() {
        return x?.floatValue();
    }
    
    double doubleValue() {
        return x?.doubleValue();
    }
    
    long longValue() {
        return x?.byteValue();
    }
    
    int intValue() {
        return x?.intValue();
    }
    
    @Override
    public int compareTo(Object o) {
        if (o == null || !(o instanceof Number)) {
            return 1;
        }
        this.compareTo(o);
    }
    
    public int compareTo(Complex o) {
        if (this.x == o.x) {
            return this.y.compareTo(o.y) ;
        }
        this.x.compareTo(o.x);
    }
    
    public int compareTo(BigDecimal o) {
        (this.x instanceof BigDecimal ? this.x : new BigDecimal(this.x)).compareTo(o);
    }
    
    public int compareTo(Number o) {
        new BigDecimal(this.x).compareTo(o);
    }
    
    /**
     * Overrides '+' operator for second operand of Number type
     */
    Complex plus(Number op) {
        if (x instanceof BigDecimal || y instanceof BigDecimal 
                || op instanceof BigDecimal) {
            return new Complex(x: MathHelper.toBigDecimal(x) + MathHelper.toBigDecimal(op), 
                y: MathHelper.toBigDecimal(y)) 
        }
        return new Complex(x: x + op, y: y);
    }
    
    /**
     * Overrides '+' operator for second operand of Complex type
     */
    Complex plus(Complex op) {
        return new Complex(x: x + op.x, y: y + op.y);
    }
    
    /**
     * Overrides '-' operator for second operand of Number type
     */
    Complex minus(Number op) {
        if (x instanceof BigDecimal || y instanceof BigDecimal 
            || op instanceof BigDecimal) {
            return new Complex(x: MathHelper.toBigDecimal(x) - MathHelper.toBigDecimal(op), 
                y: MathHelper.toBigDecimal(y)) 
        }
        new Complex(x - op, y);
    }
    
    /**
     * Overrides '-' operator for second operand of Complex type
     */
    Complex minus(Complex op) {
        new Complex(x: x - op.x, y: y - op.y);
    }
    
    /**
     * Overrides '*' operator for second operand of Number type
     */
    Complex multiply(Number op) {
        if (x instanceof BigDecimal || y instanceof BigDecimal 
            || op instanceof BigDecimal) {
            return new Complex(x: MathHelper.toBigDecimal(x) * MathHelper.toBigDecimal(op), 
                y: MathHelper.toBigDecimal(y) * MathHelper.toBigDecimal(op)) 
        }
        new Complex(x: x * op, y: y * op);
    }
    
    /**
     * Overrides '*' operator for second operand of Complex type
     */
    Complex multiply(Complex op) {
        return new Complex(x: x * op.x - y * op.y, y: x * op.y + y *op.x);
    }
    
    /**
     * Overrides '/' operator for second operand of Number type
     */
    Complex div(Number op) {
        if (x instanceof BigDecimal || y instanceof BigDecimal 
            || op instanceof BigDecimal) {
            return new Complex(x: MathHelper.toBigDecimal(x) / MathHelper.toBigDecimal(op), 
                y: MathHelper.toBigDecimal(y) / MathHelper.toBigDecimal(op)) 
        }
        new Complex(x: (x / op), y: (y / op));
    }
    
    /**
     * Overrides '/' operator for second operand of Complex type
     */
    Complex div(Complex op) {
        return this * op.conjugate() / op.abs2();
    }
    
    /**
     * returns conjugated complex, i. e. with opposite sign of y
     */
    Complex conjugate() {
        return new Complex(x: x, y: -y);
    }
    
    /**
     * returns multiplication this its conjugaed pair, i. e. abs in square
     */
    Number abs2() {
        return x * x + y * y;
    }
    
    /**
      * overrides "-" unary operator
      */
    Complex negative() {
        new Complex(x: -x, y: -y);
    }
    
    String toString() {
        return "($x, $y)";
    }
    
    /**
     * converts this to Complex with x, y of BigDecimal type
     */
    Complex toComplexBigDecimal() {
        new Complex(MathHelper.toBigDecimal(x), MathHelper.toBigDecimal(y));
    }
    
    /**
     * checks if this is zero
     */
    Boolean isZero() {
        (x.compareTo(BigDecimal.ZERO) == 0 && y.compareTo(BigDecimal.ZERO) == 0) || (!x && !y);            
    }    
    
    /**
     * checks if this is real number, i. e. with zero imaginary part
     */
    Boolean isReal() {
        !y || MathHelper.toBigDecimal(y).compareTo(BigDecimal.ZERO) == 0;
    }
    
    /**
     * converts this to real number if y-part is zero
     */ 
    Number toRealIfPossible() {
        this.isReal() ? x : this;
    }
    
    /**
     * converts x, y-parts to BigDecimal and rounds to precision
     */
    Complex roundBigDecimalToPrecision(BigDecimal precision) {
        int scale = precision.scale();
        Complex cB = this.toComplexBigDecimal();
        new Complex(cB.x.setScale(scale, BigDecimal.ROUND_HALF_UP), cB.y.setScale(scale, BigDecimal.ROUND_HALF_UP));
    }
  
    /**
     * returns x-parts of elements
     */
    static List<Number> getX(List<Number> list) {
        list?.collect{new Complex(it).x};
    }
    
    /**
     * returns y-parts of elements
     */
    static List<Number> getY(List<Number> list) {
        list?.collect{new Complex(it).y};
    }
}

