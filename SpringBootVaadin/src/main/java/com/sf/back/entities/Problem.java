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
@Getter
@Setter
@NoArgsConstructor
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "problemId", 
            fetch = FetchType.EAGER)
    private Collection<Matrix> matrixes;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Problem)) {
            return false;
        }
        Problem other = (Problem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sf.back.entities.Problem[ id=" + id + " ]";
    }
    
}
