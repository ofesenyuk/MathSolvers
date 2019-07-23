package com.sf.back.entities;

import static com.sf.back.entities.Kind.POLYNOMIAL;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import groovy.transform.*;
import org.dozer.Mapping;

/**
 *
 * @author sf
 */
@Entity
@Table(name = "problem")
@XmlRootElement
@Canonical
public class Problem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    Long id;
    @Lob
    @Column(name = "description")
    byte[] description;
    @Column(name = "matrix_dimension")
    Integer matrixDimension;
    @Column(name = "problem_precision")
    Integer problemPrecision;
    @Column(name = "is_solved")
    Boolean isSolved;
    @Column(name = "kind", nullable = false)
    @Enumerated//(EnumType.STRING)
    Kind kind = POLYNOMIAL;
    @OneToMany(mappedBy = "parentProblem", fetch = FetchType.EAGER)
    Collection<Problem> children;
    @JoinColumn(name = "parent_problem", referencedColumnName = "id")
    @ManyToOne
    Problem parentProblem;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentProblem", 
        fetch = FetchType.EAGER)
    Collection<Matrix> matrixes;

    @Override
    public String toString() {
        return "com.sf.back.entities.Problem[ id=" + id + " ]";
    }

    public void setDescription(String description) {
        this.description = description.getBytes();
    } 
    
    @Mapping("description")
    public void setDescription(byte[] description) {
        this.description = description;
    }
    
    public byte[] getDescription() {
        return this.description;
    }
}
