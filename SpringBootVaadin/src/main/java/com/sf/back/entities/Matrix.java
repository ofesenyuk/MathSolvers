/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.back.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sf
 */
@Entity
@Table(name = "matrix")
@XmlRootElement
public class Matrix implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "i")
    private Integer i;
    @Column(name = "j")
    private Integer j;
    @Column(name = "float_value")
    private Double floatValue;
    @Lob
    @Column(name = "binary_value")
    private byte[] binaryValue;
    @Column(name = "is_condition", nullable = false)
    private boolean isCondition = true;
    @JoinColumn(name = "problem_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Problem parentProblem;

    @Override
    public String toString() {
        return "com.sf.back.entities.Matrix[ id=" + id + " ]";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getI() {
        return i;
    }

    public void setI(Integer i) {
        this.i = i;
    }

    public Integer getJ() {
        return j;
    }

    public void setJ(Integer j) {
        this.j = j;
    }

    public Double getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(Double floatValue) {
        this.floatValue = floatValue;
    }

    public byte[] getBinaryValue() {
        return binaryValue;
    }

    public void setBinaryValue(byte[] binaryValue) {
        this.binaryValue = binaryValue;
    }

    public Boolean getIsCondition() {
        return isCondition;
    }

    public void setIsCondition(Boolean isCondition) {
        this.isCondition = isCondition;
    }

    public Problem getParentProblem() {
        return parentProblem;
    }

    public void setParentProblem(Problem parentProblem) {
        this.parentProblem = parentProblem;
    }
    
}
