/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sf.shared.dto

import com.sf.back.entities.Kind;
import groovy.transform.*;

/**
 *
 * @author sf
 */
@Canonical
class ProblemDTO implements Cloneable {
    Long id;
    String description;
    String problemPrecision;
    String[][] conditionArray;
    Map<String,String[][]> solution;
    Kind kind;
    
    public void setDescription(byte[] description) {
        this.description = new String(description);
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}

