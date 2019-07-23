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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    Logger LOG = LoggerFactory.getLogger(ProblemService.class);
    private final ProblemRepository problemRepository;
    private final DozerBeanMapper mapper;
    private final MatrixService matrixService;
    private static final String META_CLASS = "metaClass";
    private static final String DESCRIPTION = "description";

    @Autowired
    public ProblemService(ProblemRepository problemRepository,
                          MatrixService matrixService) {
        this.problemRepository = problemRepository;
        
        mapper = new DozerBeanMapper();
        mapper.addMapping(new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(ProblemDTO.class, Problem.class)
                    .exclude(DESCRIPTION)
                    .exclude(META_CLASS);
            }
        });
        this.matrixService = matrixService;
    }
    
    
    public List<ProblemDTO> findAll() {
        final List<Problem> problemsDb = problemRepository.findAll();
        final List<ProblemDTO> dtos = problemsDb.stream()
                .map(this::toProblemDTO)
                .collect(Collectors.toList());
        LOG.info("{} problems are found", problemsDb.size());
        return dtos;
    }

    public ProblemDTO findById(Long id) {
        return problemRepository.findById(id)
            .map(this::toProblemDTO)
            .orElseGet(() -> {
                LOG.warn("Problem with {} is not found", id);
                return null;
            });
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void save(ProblemDTO problemDTO) {
        final boolean isProblemNew = problemDTO.getId() == null;
        if (isProblemNew) {
            Problem problem = toNewProblemDB(problemDTO);
            problemRepository.save(problem);
            LOG.info("new problem with id {} is saved", problem.getId());
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
        LOG.info("problem with id {} is deveted", id);
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
//        Problem problem = toProblemDB(problemDTO);
        final Problem problem = mapper.map(problemDTO, Problem.class);
        problem.setDescription(problemDTO.getDescription().getBytes());
        final String[][] conditionArray = problemDTO.getConditionArray();
        List<Matrix> matrixes = fillMatrixes(conditionArray, problem);
        problem.setMatrixes(matrixes);
        return problem;
    }

    private Problem toProblemDB(ProblemDTO problemDTO) throws NumberFormatException {
        final Problem problem = new Problem();
        problem.setId(problemDTO.getId());
        problem.setKind(problemDTO.getKind());
        problem.setProblemPrecision(Integer.valueOf(problemDTO
                .getProblemPrecision()));
        problem.setDescription(problemDTO.getDescription().getBytes());
        return problem;
    }

    private void mergeProblemAndLinkedMatrixes(ProblemDTO problemDTO) {
        Long id = problemDTO.getId();
        final Problem problem = problemRepository.findById(id).orElse(null);
        if (problem == null) {
            LOG.warn("problem is not found in repository");
            return;
        }
//        final Problem newProblem = toProblemDB(problemDTO);
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
        LOG.info("problem with id {} is merged", problem.getId());
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

    public Map<String, String[][]> getSolution(Long id) {
        List<Matrix> solutionMatrixes = matrixService.getSolution(id);
        return null;
    }
}
