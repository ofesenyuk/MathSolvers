package com.sf.service;

import com.helger.commons.math.MathHelper;
import com.sf.back.entities.Matrix;
import com.sf.back.entities.Problem;
import com.sf.math.algebra.Polynomial;
import com.sf.math.algebra.solvers.PolynomialSolver;
import com.sf.math.number.Complex;
import com.sf.repository.MatrixRepository;
import com.sf.repository.ProblemRepository;
import groovy.lang.IntRange;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

/**
 *
 * @author sf
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MatrixService {
    private final MatrixRepository repository;
    private final ProblemRepository problemRepository;
    private final PolynomialSolver solver;

    void save(Matrix matrix) {
        repository.save(matrix);
    }

    void delete(Matrix matrix) {
        repository.delete(matrix);
    }

    List<Matrix> findSolution(Long parentProblemId) {
        final List<Matrix> solution 
                = repository.findByIsConditionAndParentProblemId(false, 
                parentProblemId);
        if (!CollectionUtils.isEmpty(solution)) {
            return solution;
        }
        log.debug("solution is not empty");
        final List<Matrix> conditions = repository
                .findByIsConditionAndParentProblemId(true, parentProblemId);
        log.debug("conditions [{}]", conditions);
        if (CollectionUtils.isEmpty(conditions)) {
            return solution;
        }
        conditions.sort(Comparator.comparing(Matrix::getI)
                .thenComparing(Comparator.comparing(Matrix::getJ)));
        final List<Number> coefficients = conditions.stream().map(m -> {
            final byte[] binaryValue = m.getBinaryValue();
            if (binaryValue != null) {
                return new BigDecimal(new String(binaryValue));
            } 
            return m.getFloatValue();
        }).collect(Collectors.toList());
        final Polynomial p = new Polynomial(coefficients);
        final Problem problem = problemRepository.getOne(parentProblemId);
        final Integer precision = problem.getProblemPrecision();
        final List<Number> roots = solver.findRoots(p, Math.pow(10, -precision));
        List<Matrix> calculatedSolution = IntStream.range(0, roots.size())
            .mapToObj(j -> {
                final Number r = roots.get(j);
                final Complex c = new Complex(r);
                final Matrix matrix = new Matrix();
                matrix.setI(0);
                matrix.setJ(j);
                matrix.setIsCondition(false);
                matrix.setParentProblem(problem);
                if (c.isReal() && Double.isFinite(r.doubleValue())) {
                    matrix.setFloatValue(MathHelper.toBigDecimal(r)
                            .setScale(precision, RoundingMode.HALF_UP)
                            .doubleValue());
                } else {
                    matrix.setBinaryValue(c.roundBigDecimalToScale(precision)
                            .toString().getBytes());
                }
                return matrix;
            })
            .collect(Collectors.toList());
        return calculatedSolution;
    }
    
}
