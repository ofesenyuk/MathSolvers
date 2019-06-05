package com.sf.service;

import com.sf.back.entities.Matrix;
import com.sf.back.entities.Problem;
import com.sf.repository.ProblemRepository;
import com.sf.shared.dto.ProblemDTO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.dozer.DozerBeanMapper;
import org.dozer.loader.api.BeanMappingBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author sf
 */
@Service
public class ProblemService {
    private final ProblemRepository problemRepository;
    private final DozerBeanMapper mapper;
    private final MatrixService matrixService;

    @Autowired
    public ProblemService(ProblemRepository problemRepository,
                          MatrixService matrixService) {
        this.problemRepository = problemRepository;
        
        mapper = new DozerBeanMapper();
        mapper.addMapping(new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(ProblemDTO.class, Problem.class).exclude("description");
            }
        });
        this.matrixService = matrixService;
    }
    
    
    public List<ProblemDTO> findAll() {
        final List<Problem> problemsDb = problemRepository.findAll();
        final List<ProblemDTO> dtos = problemsDb.stream()
                .map(this::toProblemDTO)
                .collect(Collectors.toList());
        return dtos;
    }

    public ProblemDTO findById(Long id) {
        return problemRepository.findById(id).map(this::toProblemDTO).orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void save(ProblemDTO problemDTO) {
        final boolean isProblemNew = problemDTO.getId() == null;
        if (isProblemNew) {
            Problem problem = toNewProblemDB(problemDTO);
            problemRepository.save(problem);
            return;
        }
        mergeProblemAndLinkedMatrixes(problemDTO);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(ProblemDTO problem) {
        final Long id = problem.getId();
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("irregular id " + id 
                    + " for deletion");
        }
        problemRepository.deleteById(id);
    }
    
    private  ProblemDTO toProblemDTO(Problem problem) {
        ProblemDTO problemDTO = mapper.map(problem, ProblemDTO.class);
        problemDTO.setDescription(problem.getDescription());
        final Collection<Matrix> matrixes = problem.getMatrixes();
        final List<Matrix> conditionList = matrixes.stream()
            .filter(Matrix::getIsCondition)
            .filter(m -> m.getI() != null)
            .filter(m -> m.getJ() != null)
            .collect(Collectors.toList());
        final int nI = 1 + conditionList.stream()
                .mapToInt(Matrix::getI)
                .max()
                .orElse(0);
        final int nJ = 1 + conditionList.stream()
                .mapToInt(Matrix::getJ)
                .max().orElse(0);
        String[][] conditionAray = new String[nI][nJ];
        for (Matrix m: conditionList) {
            final Double floatValue = m.getFloatValue();
            if (floatValue != null) {
                conditionAray[m.getI()][m.getJ()] = floatValue.toString();
            } else if (m.getBinaryValue() != null) {
                conditionAray[m.getI()][m.getJ()] 
                    = new String(m.getBinaryValue());
            }
        }
        problemDTO.setConditionArray(conditionAray);
        return problemDTO;
    }

    private Problem toNewProblemDB(ProblemDTO problemDTO) {
        final Problem problem = mapper.map(problemDTO, Problem.class);
        problem.setDescription(problemDTO.getDescription().getBytes());
        final String[][] conditionArray = problemDTO.getConditionArray();
        List<Matrix> matrixes = fillMatrixes(conditionArray, problem);
        problem.setMatrixes(matrixes);
        return problem;
    }

    private void mergeProblemAndLinkedMatrixes(ProblemDTO problemDTO) {
        Long id = problemDTO.getId();
        final Problem problem = problemRepository.findById(id).orElse(null);
        if (problem == null) {
            return;
        }
        final Problem newProblem = mapper.map(problemDTO, Problem.class);
        newProblem.setDescription(problemDTO.getDescription().getBytes());
        final Map<Pair<Integer,Integer>,Matrix> conditionMatrixes = problem
                .getMatrixes().stream()
                .filter(Matrix::getIsCondition)
                .filter(m -> m.getI() != null)
                .filter(m -> m.getJ() != null)
                .collect(Collectors.toMap(m -> Pair.of(m.getI(), m.getJ()), 
                                          m -> m));
        final String[][] conditionArray = problemDTO.getConditionArray();
        for (int i = 0; i < conditionArray.length; i++) {
            for (int j = 0; j < conditionArray[i].length; j++) {
                final String condition = problemDTO.getConditionArray()[i][j];
                Double doubleValue = Double.valueOf(condition);
                final Pair<Integer, Integer> pointer = Pair.of(i, j);
                Matrix matrix = conditionMatrixes.get(pointer);
                if (matrix == null) {
                    if (Objects.equals(doubleValue, 0.0d)) {
                        continue;
                    }
                    matrix = new Matrix();
                    matrix.setI(i);
                    matrix.setJ(j);
                    matrix.setParentProblem(newProblem);
                    problem.getMatrixes().add(matrix);
                } else {
                    conditionMatrixes.remove(pointer);
                }
                if (doubleValue.isInfinite()) {
                    final byte[] oldValue = matrix.getBinaryValue();
                    if (oldValue == null
                            || !new String(oldValue).equals(condition)) {
                        matrix.setBinaryValue(condition.getBytes());
                        matrix.setFloatValue(null);
                    }
                } else if (!Objects.equals(doubleValue, matrix.getFloatValue())) {
                    matrix.setFloatValue(doubleValue);
                    matrix.setBinaryValue(null);
                }
            }
        }
        conditionMatrixes.values().forEach(matrix -> {
            matrixService.delete(matrix);
            problem.getMatrixes().remove(matrix);
        });
        problem.setDescription(newProblem.getDescription());
        problem.setKind(newProblem.getKind());
        problemRepository.save(problem);
    }

    private List<Matrix> fillMatrixes(String[][] conditionArray, 
            Problem problem) {
        List<Matrix> matrixes = new ArrayList<>();
        for (int i = 0; i < conditionArray.length; i++) {
            String[] row = conditionArray[i];
            for (int j = 0; j < row.length; j++) {
                final Matrix matrix = new Matrix();
                final double value = Double.parseDouble(row[j]);
                if (Double.isInfinite(value)) {
                    matrix.setBinaryValue(row[j].getBytes());
                } else {
                    matrix.setFloatValue(value);
                }
                matrix.setI(i);
                matrix.setJ(j);
                matrix.setIsCondition(true);
                matrix.setParentProblem(problem);
                matrixes.add(matrix);
            }
        }
        return matrixes;
    }
}
