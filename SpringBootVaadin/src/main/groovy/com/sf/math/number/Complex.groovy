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
}

