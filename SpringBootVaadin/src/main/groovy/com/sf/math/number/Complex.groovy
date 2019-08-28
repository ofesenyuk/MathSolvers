/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sf.math.number

/**
 *
 * @author sf
 */
class Complex extends Number {
    Number x;
    Number y;
    
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
    
    Complex plus(Number op) {
        return new Complex(x: x + op, y: y);
    }
    
    Complex plus(Complex op) {
        return new Complex(x: x + op.x, y: y + op.y);
    }
    
    Complex minus(Number op) {
        return new Complex(x: x - op, y: y);
    }
    
    Complex minus(Complex op) {
        return new Complex(x: x - op.x, y: y - op.y);
    }
    
    Complex multiply(Number op) {
        return new Complex(x: x * op, y: y * op);
    }
    
    Complex multiply(Complex op) {
        return new Complex(x: x * op.x - y * op.y, y: x * op.y + y *op.x);
    }
    
    Complex div(Number op) {
        return new Complex(x: (x / op), y: (y / op));
    }
    
    Complex div(Complex op) {
        return this * op.conjugate() / op.abs2();
    }
    
    Complex conjugate() {
        return new Complex(x: x, y: -y);
    }
    
    Number abs2() {
        return x *x + y *y;
    }
}

