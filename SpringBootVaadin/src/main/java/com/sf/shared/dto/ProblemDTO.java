/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.shared.dto;

import com.sf.back.entities.Kind;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author sf
 */
//@Getter
//@Setter
public class ProblemDTO {
    private String description;
    private String[][] conditionArray;
    private Map<String,String[][]> solution;
    private Kind kind;

    public void setDescription(byte[] description) {
        this.description = new String(description);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String,String[][]> getSolution() {
        return solution;
    }

    public void setSolution(Map<String,String[][]> solution) {
        this.solution = solution;
    }

    public String[][] getConditionArray() {
        return conditionArray;
    }

    public void setConditionArray(String[][] conditionArray) {
        this.conditionArray = conditionArray;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }
}
