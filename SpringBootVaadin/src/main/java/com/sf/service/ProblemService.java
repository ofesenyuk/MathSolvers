package com.sf.service;

import com.github.dozermapper.core.DozerBeanMapper;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import com.sf.back.entities.Matrix;
import com.sf.back.entities.Problem;
import com.sf.repository.ProblemRepository;
import com.sf.shared.dto.ProblemDTO;
import com.sf.utilites.MatrixUtilites;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ProblemService {
    private static final String META_CLASS = "metaClass";
    private static final String DESCRIPTION = "description";
    private Logger LOG = LoggerFactory.getLogger(ProblemService.class);
    private final ProblemRepository problemRepository;
//    private final DozerBeanMapper mapper;
    private final MatrixService matrixService;
    private final MatrixUtilites matrixUtilites;
    private final Mapper mapper;    
    
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<ProblemDTO> findAll() {
        final List<Problem> problemsDb = problemRepository.findAll();
        final List<ProblemDTO> dtos = problemsDb.stream()
                .map(this::toProblemDTO)
                .collect(Collectors.toList());
        LOG.info("{} problems are found", problemsDb.size());
        return dtos;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public ProblemDTO findById(Long id) {
        final ProblemDTO p = problemRepository.findById(id)
                .map(this::toProblemDTO)
                .orElseGet(() -> {
                    LOG.warn("Problem with {} is not found", id);
                    return null;
                });
        
        LOG.info("Problem with {} is found", id);
        return p;
    }

    /**
     * converts problemDTO to problem with dependent matrixes and saves them
     * @param problemDTO of ProblemDTO type
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void save(ProblemDTO problemDTO) {
        if (problemDTO == null) {
            throw new IllegalArgumentException("problemDTO is not set "
                    + "for save");
        }
        final boolean isProblemNew = problemDTO.getId() == null;
        if (isProblemNew) {
            Problem problem = toNewProblemDB(problemDTO);
            save(problem);
            LOG.info("new problem with id {} is saved", problem.getId());
            return;
        }
        mergeProblemAndLinkedMatrixes(problemDTO);
    }

    /**
     * deletes problem in DB corresponding to problem of ProblemDTO type
     * @param problem of ProblemDTO type; 
     * id of problem in DB is required
     */
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
    
    /**
     * converts problem and connected matrixes in DB to problemDTO 
     * @param problem in DB
     * @return ProblemDTO with condition matrixes
     * TODO include not only condition matrixes
     * TODO use Spring converters
     */
    private  ProblemDTO toProblemDTO(Problem problem) {
        ProblemDTO problemDTO = mapper.<ProblemDTO>map(problem, ProblemDTO.class);
        problemDTO.setDescription(problem.getDescription());
        final Integer precision = problem.getProblemPrecision();
        problemDTO.setProblemPrecision(precision != null 
                ? precision.toString() : null);
        
        final Collection<Matrix> matrixes = problem.getMatrixes();
        final List<Matrix> conditionList 
                = matrixUtilites.getMatrixesAsList(matrixes.stream()
                .filter(Matrix::getIsCondition)
                .collect(Collectors.toList()));
        String[][] conditionAray = toArray(conditionList);
        problemDTO.setConditionArray(conditionAray);
        
        if (!Boolean.TRUE.equals(problem.getIsSolved())) {
            problemDTO.setSolution(null);
            return problemDTO;
        }
        
        final List<Matrix> solutionList 
                = matrixUtilites.getMatrixesAsList(matrixes.stream()
                .filter(m -> !m.getIsCondition())
                .collect(Collectors.toList()));
        String[][] solutionAray = toArray(solutionList);
        problemDTO.setSolution(solutionAray.length > 0 
                ? Collections.singletonMap("", solutionAray)
                : null);
        
        return problemDTO;
    }

    private String[][] toArray(final List<Matrix> list) {
        Pair<Integer,Integer> dimensions
                = matrixUtilites.getMatrixDimensions(list);
        final int nI = dimensions.getFirst();
        final int nJ = dimensions.getSecond();
        String[][] array = new String[nI][nJ];
        list.forEach((m) -> {
            final Double floatValue = m.getFloatValue();
            if (floatValue != null) {
                array[m.getI()][m.getJ()] = floatValue.toString();
            } else if (m.getBinaryValue() != null) {
                array[m.getI()][m.getJ()]
                        = new String(m.getBinaryValue());
            }
        });
        return array;
    }

    /**
     * converts new problemDTO to problem in DB, creates new matrixes in DB, and 
     * connects to it
     * @param problemDTO of ProblemDTO type
     * @return Problem to be written to DB
     */
    private Problem toNewProblemDB(ProblemDTO problemDTO) {
//        Problem problem = toProblemDB(problemDTO);
        final Problem problem = mapper.map(problemDTO, Problem.class);
        problem.setDescription(problemDTO.getDescription().getBytes());
        
        final String[][] conditionArray = problemDTO.getConditionArray();
        List<Matrix> matrixes = fillMatrixes(conditionArray, problem);
        problem.setMatrixes(matrixes);
        return problem;
    }

    /**
     * Not used because of usage mapper
     * @param problemDTO
     * @return
     * @throws NumberFormatException 
     */
    private Problem toProblemDB(ProblemDTO problemDTO) throws NumberFormatException {
        final Problem problem = new Problem();
        problem.setId(problemDTO.getId());
        problem.setKind(problemDTO.getKind());
        problem.setProblemPrecision(Integer.valueOf(problemDTO
                .getProblemPrecision()));
        problem.setDescription(problemDTO.getDescription().getBytes());
        return problem;
    }

    /**
     * constructs new problem from problemDTO (with id);
     * replaces matrixes linked to problem with that of problemDTO;
     * matrixes with indexes outside that of problemDTO are deleted
     * 
     * @param problemDTO from front-end
     */
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
        
        final Map<Pair<Integer,Integer>,Matrix> conditionMatrixesDB 
                = matrixUtilites.getMatrixesAsMap(problem.getMatrixes(), true);
        final String[][] conditionArray = problemDTO.getConditionArray();
        
        for (int i = 0; i < conditionArray.length; i++) {
            for (int j = 0; j < conditionArray[i].length; j++) {
                final String condition = problemDTO.getConditionArray()[i][j];
                Double doubleValue = Double.valueOf(condition);
                final Pair<Integer, Integer> pointer = Pair.of(i, j);
                Matrix matrix = conditionMatrixesDB.get(pointer);
                if (matrix == null) {
                    if (isAbsent(doubleValue)) {
                        continue;
                    }
                    matrix = addNewMatrix(i, j, newProblem, problem);
                } else {
                    conditionMatrixesDB.remove(pointer);
                }
                if (doubleValue.isInfinite()) {
                    migrateToBinaryValue(matrix, condition);
                } else if (!Objects
                        .equals(doubleValue, matrix.getFloatValue())) {
                    migrateToFloatValue(matrix, doubleValue);
                }
            }
        }
        
        conditionMatrixesDB.values().forEach(matrix -> {
            matrixService.delete(matrix);
            problem.getMatrixes().remove(matrix);
        });
        problem.setDescription(newProblem.getDescription());
        problem.setKind(newProblem.getKind());
        save(problem);
        LOG.info("problem with id {} is merged", problem.getId());
    }

    private void migrateToFloatValue(Matrix matrix, Double doubleValue) {
        matrix.setFloatValue(doubleValue);
        matrix.setBinaryValue(null);
    }

    private void migrateToBinaryValue(Matrix matrix, final String condition) {
        final byte[] oldValue = matrix.getBinaryValue();
        if (oldValue == null
                || !new String(oldValue).equals(condition)) {
            matrix.setBinaryValue(condition.getBytes());
            matrix.setFloatValue(null);
        }
    }

    /**
     * create a new matrix and add it to newProblem and problem
     * @param i
     * @param j
     * @param newProblem
     * @param problem
     * @return 
     */
    private Matrix addNewMatrix(int i, int j, 
            final Problem newProblem, final Problem problem) {
        Matrix matrix = new Matrix();
        matrix.setI(i);
        matrix.setJ(j);
        matrix.setParentProblem(newProblem);
        problem.getMatrixes().add(matrix);
        return matrix;
    }

    /**
     * checks if doubleValue is given (not zero)
     * @param doubleValue
     * @return 
     */
    private static boolean isAbsent(Double doubleValue) {
        return Objects.equals(doubleValue, 0.0d);
    }

    /**
     * creates and fills matrixes in DB with conditionArray data 
     * (from ProblemDTO)
     * @param conditionArray condition Array from ProblemDTO
     * @param problem in DB to be connected with matrixes
     * @return 
     */
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
        final Pair<Integer, Integer> dims 
            = matrixUtilites.getMatrixDimensions(solutionMatrixes);
        String[][] solutionArray 
            = new String[dims.getFirst()][dims.getSecond()];
        solutionMatrixes.forEach(m -> solutionArray[m.getI()][m.getJ()] 
                = m.getBinaryValue() != null 
                ? new String(m.getBinaryValue())
                : m.getFloatValue().toString()
        );
        Map<String, String[][]> solution = Collections.singletonMap("", 
            solutionArray);
        return solution;
    }

    public Problem save(Problem problem) {
        validateMatrixes(problem);
                
        return problemRepository.save(problem);
    }

    private void validateMatrixes(Problem problem) {
        List<Matrix> conditionMatrixes = problem.getMatrixes().stream()
            .filter(Matrix::getIsCondition)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        checkForCoincidingPositions(conditionMatrixes);
        
        List<Matrix> solutionMatrixes = problem.getMatrixes().stream()
            .filter(m -> !m.getIsCondition())
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        checkForCoincidingPositions(solutionMatrixes);
    }

    private void checkForCoincidingPositions(List<Matrix> matrixes) {
        final List<Pair<Integer, Integer>> indexesList = matrixes.stream()
                .map(m -> Pair.of(m.getI(), m.getJ()))
                .collect(Collectors.toList());
        final HashSet<Pair<Integer, Integer>> indexesSet 
                = new HashSet<>(indexesList);
        if (indexesList.size() != indexesSet.size()) {
            throw new IllegalArgumentException("there are coinciding pairs "
                    + "among matrix indexes:" + indexesList);
        }
    }
}

