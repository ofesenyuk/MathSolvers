/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.utilites;

import com.sf.back.entities.Matrix;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

/**
 *
 * @author sf
 */
@Component
public class MatrixUtilites {
    
    public Pair<Integer, Integer> getMatrixDimensions(
            Collection<Matrix> matrixes
    ) {
        final List<Matrix> filteredMatrixes = matrixes.stream()
                .filter(m -> m.getI() != null)
                .filter(m -> m.getJ() != null)
                .collect(Collectors.toList());
        final int nI = 1 + filteredMatrixes.stream()
                .mapToInt(Matrix::getI)
                .max()
                .orElse(0);
        final int nJ = 1 + filteredMatrixes.stream()
                .mapToInt(Matrix::getJ)
                .max().orElse(0);
        return Pair.of(nI, nJ);
    }

    public List<Matrix> getMatrixesAsList(Collection<Matrix> matrixes) {
        return matrixes.stream()
                .filter(m -> m.getI() != null)
                .filter(m -> m.getJ() != null)
                .collect(Collectors.toList());
    }

    /**
     * created map of pairs matrix indexes to matrix
     * 
     * @param matrixes to map
     * @param isCondition
     * @return map 
     */
    public Map<Pair<Integer, Integer>, Matrix> getMatrixesAsMap(
            Collection<Matrix> matrixes, boolean isCondition) {
        return matrixes.stream()
            .filter(m -> m.getIsCondition() == isCondition)
            .filter(m -> m.getI() != null)
            .filter(m -> m.getJ() != null)
            .collect(Collectors.toMap(m -> Pair.of(m.getI(), m.getJ()), 
                                      m -> m));
    }
}
