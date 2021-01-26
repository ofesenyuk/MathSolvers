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
import lombok.Data;

/**
 *
 * @author OFeseniuk
 */
@Data
@Entity
@Table(name = "matrix")
public class Matrix implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    @EmbeddedId
//    protected MatrixPK matrixPK;
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
    private Boolean isCondition = true;
    @JoinColumn(name = "problem_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Problem parentProblem;
}
