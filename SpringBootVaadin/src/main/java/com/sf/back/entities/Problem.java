/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.back.entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author sf
 */
@Entity
@Table(name = "problem")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Problem.findAll", query = "SELECT p FROM Problem p")
    , @NamedQuery(name = "Problem.findById", query = "SELECT p FROM Problem p WHERE p.id = :id")
    , @NamedQuery(name = "Problem.findByMatrixDimension", query = "SELECT p FROM Problem p WHERE p.matrixDimension = :matrixDimension")
    , @NamedQuery(name = "Problem.findByIsSolved", query = "SELECT p FROM Problem p WHERE p.isSolved = :isSolved")})
//@Getter
//@Setter
//@NoArgsConstructor
//@EqualsAndHashCode(of = {"id"})
public class Problem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Lob
    @Column(name = "description")
    private byte[] description;
    @Column(name = "matrix_dimension")
    private Integer matrixDimension;
    @Column(name = "is_solved")
    private Boolean isSolved;
    @Column(name = "kind")
    @Enumerated//(EnumType.STRING)
    private Kind kind;
    @OneToMany(mappedBy = "parentProblem", fetch = FetchType.EAGER)
    private Collection<Problem> children;
    @JoinColumn(name = "parent_problem", referencedColumnName = "id")
    @ManyToOne
    private Problem parentProblem;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentProblem", 
            fetch = FetchType.EAGER)
    private Collection<Matrix> matrixes;

    @Override
    public String toString() {
        return "com.sf.back.entities.Problem[ id=" + id + " ]";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getDescription() {
        return description;
    }

    public void setDescription(byte[] description) {
        this.description = description;
    }

    public Integer getMatrixDimension() {
        return matrixDimension;
    }

    public void setMatrixDimension(Integer matrixDimension) {
        this.matrixDimension = matrixDimension;
    }

    public Boolean getIsSolved() {
        return isSolved;
    }

    public void setIsSolved(Boolean isSolved) {
        this.isSolved = isSolved;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public Collection<Problem> getChildren() {
        return children;
    }

    public void setChildren(Collection<Problem> children) {
        this.children = children;
    }

    public Problem getParentProblem() {
        return parentProblem;
    }

    public void setParentProblem(Problem parentProblem) {
        this.parentProblem = parentProblem;
    }

    public Collection<Matrix> getMatrixes() {
        return matrixes;
    }

    public void setMatrixes(Collection<Matrix> matrixes) {
        this.matrixes = matrixes;
    }
    
}
