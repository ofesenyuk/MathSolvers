/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sf.shared.dto;

import com.sf.back.entities.Kind;
import groovy.transform.ToString;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author OFeseniuk
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ProblemDTO implements Cloneable {
    private Long id;
    private String description;
    private String problemPrecision;
    private String[][] conditionArray;
    private Map<String, String[][]> solution;
    private Kind kind;
    
    public void setDescription(byte[] description) {
        this.description = new String(description);
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
