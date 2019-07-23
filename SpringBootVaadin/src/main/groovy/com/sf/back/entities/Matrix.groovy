package com.sf.back.entities

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;
import groovy.transform.*;
/**
 *
 * @author sf
 */
@Entity
@Table(name = "matrix")
@XmlRootElement
@Canonical
public class Matrix implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
//    @EmbeddedId
//    protected MatrixPK matrixPK;
    @Column(name = "i")
    Integer i;
    @Column(name = "j")
    Integer j;
    @Column(name = "float_value")
    Double floatValue;
    @Lob
    @Column(name = "binary_value")
    byte[] binaryValue;
    @Column(name = "is_condition", nullable = false)
    Boolean isCondition = true;
    @JoinColumn(name = "problem_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    Problem parentProblem;
}

