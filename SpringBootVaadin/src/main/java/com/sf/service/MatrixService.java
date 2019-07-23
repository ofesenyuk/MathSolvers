package com.sf.service;

import com.sf.back.entities.Matrix;
import com.sf.repository.MatrixRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author sf
 */
@Service
public class MatrixService {
    private final MatrixRepository repository;

    public MatrixService(MatrixRepository repository) {
        this.repository = repository;
    }

    void save(Matrix matrix) {
        repository.save(matrix);
    }

    void delete(Matrix matrix) {
        repository.delete(matrix);
    }

    List<Matrix> getSolution(Long id) {
        return repository.findByIsConditionAndParentProblemId(false, id);
    }
    
}
