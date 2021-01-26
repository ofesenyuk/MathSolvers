/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.service;

import com.sf.VaadinMathSolverApplication;
import com.sf.back.entities.Kind;
import com.sf.back.entities.Matrix;
import com.sf.back.entities.Problem;
import com.sf.repository.ProblemRepository;
import com.sf.shared.dto.ProblemDTO;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.DataAccessException;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.annotation.Rollback;

import javax.persistence.Table;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.sql.Types.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 *
 * @author sf
 */
//@RunWith(SpringRunner.class)
@SpringBootTest(
        //  SpringBootTest.WebEnvironment.MOCK,
        classes = VaadinMathSolverApplication.class)
//@DataJpaTest
@Rollback
public class ProblemServiceTest {

    private static final String PROBLEM
            = Problem.class.getAnnotation(Table.class).name();
    private static final String MATRIX
            = Matrix.class.getAnnotation(Table.class).name();
    private static final String SELECT_PROBLEM_IDS
            = "SELECT id FROM " + PROBLEM;

    private static final String ID = "id";
    private static final String DESCRIPTION = "description";
    private static final String KIND = "kind";
    private static final String PROBLEM_PRECISION = "problem_precision";
    private static final String PROBLEM_ID = "problem_id";
    private static final String IS_SOLVED = "is_solved";

    private static final String I = "i";
    private static final String J = "j";
    private static final String FLOAT_VALUE = "float_value";
    private static final String BINARY_VALUE = "binary_value";
    private static final String IS_CONDITION = "is_condition";
    private static final String MATRIX_DIMENSION = "matrix_dimension";

    private static final String SELECT_PROBLEM
            = "SELECT " + PROBLEM + "." + ID + ", " + DESCRIPTION + ", " + KIND
            + ", " + PROBLEM_PRECISION + ", " + I + ", " + J
            + ", " + FLOAT_VALUE + ", " + BINARY_VALUE + ", " + IS_CONDITION
            + " FROM " + PROBLEM + " LEFT JOIN " + MATRIX + " ON "
            + MATRIX + "." + PROBLEM_ID + " = " + PROBLEM + "." + ID
            + " WHERE " + PROBLEM + "." + ID + " = :" + ID;

    Long savedProblemId;

    @SpyBean
    private ProblemService problemService;
    @Autowired
    private ProblemRepository problemRepository;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Captor
    ArgumentCaptor<Problem> problemCaptor;

    public ProblemServiceTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() throws UnsupportedEncodingException {
        savedProblemId = createProblem();
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of findAll method, of class ProblemService.
     */
    @Test
    public void testFindAll() {
        System.out.println("findAll");
        final Set<Long> expResult = new HashSet<>(getAllIds());
        Set<Long> result = problemService.findAll().stream()
                .map(ProblemDTO::getId)
                .collect(Collectors.toSet());
        assertEquals(expResult, result, "id sets are not equal");
    }

    /**
     * Test of findById method, of class ProblemService.
     */
    @Test
    public void testFindById() {
        System.out.println("findById");
        List<Pair<ProblemDTO, Matrix>> problemMatrixes
                = namedParameterJdbcTemplate.query(SELECT_PROBLEM,
                        Collections.singletonMap(ID, savedProblemId), this::toProblemMatrix);
        int nI = 1 + problemMatrixes.stream()
                .mapToInt(pm -> pm.getSecond().getI())
                .max()
                .getAsInt();
        int nJ = 1 + problemMatrixes.stream()
                .mapToInt(pm -> pm.getSecond().getJ())
                .max()
                .getAsInt();
        ProblemDTO expResult = problemMatrixes.get(0).getFirst();
        expResult.setConditionArray(new String[nI][nJ]);
        problemMatrixes.forEach(pm -> {
            final Matrix m = pm.getSecond();
            final String[][] condition = expResult.getConditionArray();
            condition[m.getI()][m.getJ()] = m.getBinaryValue() != null
                    ? new String(m.getBinaryValue())
                    : m.getFloatValue().toString();
        });

        ProblemDTO result = problemService.findById(savedProblemId);
        assertEquals(expResult, result);
    }

    /**
     * Test of save method, of class ProblemService.
     */
    @Test
    @Rollback
    public void testSave_ProblemDTO() {
        System.out.println("save");
        ProblemDTO problemDTO = problemService.findById(savedProblemId);
        problemDTO.setDescription(DESCRIPTION + DESCRIPTION);
        final String cell = problemDTO.getConditionArray()[0][0];
        problemDTO.getConditionArray()[0][0] = cell + "1";
        problemService.save(problemDTO);
        final ProblemDTO problemFound = problemService.findById(savedProblemId);
        assertEquals(problemDTO, problemFound, "Problem is not updated.");
    }

    /**
     * Test of save method, of class ProblemService.
     */
    @Test
    @Rollback
    public void testSave_NewProblemDTO() {
        System.out.println("saveNew");
        ProblemDTO problemDTO = new ProblemDTO();
        problemDTO.setDescription(DESCRIPTION);
        problemDTO.setKind(Kind.POLYNOMIAL);
        problemDTO.setProblemPrecision("24");
        problemDTO
                .setConditionArray(new String[][]{{"1.0", "2.0", "3.0", "4.0"}});

        problemService.save(problemDTO);

        Mockito.verify(problemService)
                .save(problemCaptor.capture());
        final long savedId = problemCaptor.getValue().getId();
        problemDTO.setId(savedId);
        final ProblemDTO problemFound = problemService.findById(savedId);
        assertEquals(problemDTO, problemFound, "Problem is not saved.");
    }

    /**
     * Test of delete method, of class ProblemService.
     */
    @Test
    public void testDelete() {
        System.out.println("delete");
        ProblemDTO problem = problemService.findById(savedProblemId);
        problemService.delete(problem);
        assertNull(problemService.findById(savedProblemId));
    }


    /**
     * Test of getSolution method, of class ProblemService.
     */
    @Test
    public void testGetSavedSolution() {
        System.out.println("getSolution");
        Long id = createProblemWithSolution();
        ProblemDTO problemDTO = problemService.findById(id);
        Map<String, String[][]> expResult = problemDTO.getSolution();
        Map<String, String[][]> result = problemService.getSolution(id);
        assertEquals(expResult.keySet(), result.keySet());
        expResult.forEach((key, expArray) -> {
            String[][] resArray = result.get(key);
            for (int i = 0; i < expArray.length; i++) {
                for (int j = 0; j < expArray[0].length; j++) {
                    assertEquals(expArray[i][j], resArray[i][j],
                            "cells [" + i + "][" + j + "] must be equal");
                }
            }
        });
    }

    /**
     * Test of save method, of class ProblemService.
     */
    @Test
    public void testSave_NewProblem() {
        System.out.println("save");
        Problem expResult = new Problem();
        expResult.setDescription(DESCRIPTION);
        expResult.setIsSolved(false);
        expResult.setKind(Kind.POLYNOMIAL);
        expResult.setMatrixDimension(12);
        expResult.setMatrixes(IntStream.range(0, 10).mapToObj(j -> {
            Matrix m = new Matrix();
            m.setI(0);
            m.setJ(j);
            m.setFloatValue(0.0d + j);
            m.setIsCondition(true);
            m.setParentProblem(expResult);
            return m;
        })
                .collect(Collectors.toList()));
        Problem result = problemService.save(expResult);
        assertEquals(expResult, result);
    }

    /**
     * Test of save method, of class ProblemService.
     */
    @Test
    public void testSave_Problem() {
        System.out.println("save");
        Problem expResult = problemRepository.findById(savedProblemId)
                .orElseThrow(() -> new RuntimeException("problem is not found for "
                + "provided id"));
        expResult.setDescription((DESCRIPTION + PROBLEM).getBytes());
        expResult.getMatrixes().stream().findAny()
                .ifPresent((m) -> m.setFloatValue(m.getFloatValue() + 1));
        Problem result = problemService.save(expResult);
        assertEquals(new String(expResult.getDescription()),
                new String(result.getDescription()));
        assertEquals(expResult.getId(), result.getId());
        Map<Pair<Integer, Integer>, Matrix> expMatrixes = expResult.getMatrixes()
                .stream()
                .collect(Collectors.toMap(m -> Pair.of(m.getI(), m.getJ()),
                        m -> m));
        Map<Pair<Integer, Integer>, Matrix> resMatrixes = result.getMatrixes()
                .stream()
                .collect(Collectors.toMap(m -> Pair.of(m.getI(), m.getJ()),
                        m -> m));
        expMatrixes.forEach((pair, expM) -> {
            Matrix resM = resMatrixes.get(pair);
            assertEquals(expM.getBinaryValue() != null
                    ? new String(expM.getBinaryValue())
                    : null,
                    resM.getBinaryValue() != null
                    ? new String(resM.getBinaryValue())
                    : null);
            assertEquals(expM.getFloatValue(), resM.getFloatValue());
        });
    }

    private List<Long> getAllIds() throws DataAccessException {
        return jdbcTemplate.queryForList(SELECT_PROBLEM_IDS, Long.class);
    }

    private Pair<ProblemDTO, Matrix> toProblemMatrix(ResultSet rs,
            int rowNum) {
        ProblemDTO problemDTO = new ProblemDTO();
        Matrix matrix = new Matrix();
        try {
            if (rowNum == 0) {
                problemDTO.setId(rs.getLong(ID));
                problemDTO.setDescription(rs.getBytes(DESCRIPTION));
                problemDTO.setKind(Kind.valueOf(rs.getString(KIND)));
                problemDTO.setProblemPrecision(rs.getString(PROBLEM_PRECISION));
            }

            final Integer i = Integer.valueOf(rs.getString(I));
            final Integer j = Integer.valueOf(rs.getString(J));
            final double floatValue = rs.getDouble(FLOAT_VALUE);
            final Blob blob = rs.getBlob(BINARY_VALUE);
            final byte[] binaryValue
                    = blob != null
                            ? blob.getBytes(0, Long.valueOf(blob.length()).intValue())
                            : null;
            if (i != null && j != null) {
                matrix.setI(i);
                matrix.setJ(j);
                if (binaryValue != null) {
                    matrix.setBinaryValue(binaryValue);
                } else {
                    matrix.setFloatValue(floatValue);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProblemServiceTest.class.getName())
                    .log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
        return Pair.of(problemDTO, matrix);
    }

    @Transactional
    private Long createProblem() throws UnsupportedEncodingException {
        SqlUpdate problemIserter = new SqlUpdate(dataSource,
                "INSERT INTO " + PROBLEM + " (" + DESCRIPTION + ", "
                + KIND + ", " + PROBLEM_PRECISION + ", " + MATRIX_DIMENSION
                + ") VALUES(:" + DESCRIPTION + ", :"
                + KIND + ", :" + PROBLEM_PRECISION + ", :" + MATRIX_DIMENSION
                + ")");
        problemIserter.declareParameter(new SqlParameter(DESCRIPTION,
                Types.VARCHAR));
        problemIserter.declareParameter(new SqlParameter(KIND, Types.VARCHAR));
        problemIserter.declareParameter(new SqlParameter(PROBLEM_PRECISION,
                Types.NUMERIC));
        problemIserter.declareParameter(new SqlParameter(MATRIX_DIMENSION,
                Types.NUMERIC));
        Map<String, Object> paramMap = new HashMap<>();
        final String descr = PROBLEM + DESCRIPTION;
        paramMap.put(DESCRIPTION, toHexCodes(descr));
        paramMap.put(KIND, Kind.POLYNOMIAL);
        paramMap.put(PROBLEM_PRECISION, 22);
        paramMap.put(MATRIX_DIMENSION, 1);
        problemIserter.setReturnGeneratedKeys(true);
        problemIserter.setGeneratedKeysColumnNames(ID);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        problemIserter.updateByNamedParam(paramMap, keyHolder);
        long problemId = Optional.ofNullable(keyHolder.getKey())
                .map(Number::longValue)
                .orElseThrow(() -> new RuntimeException("Problem id is not "
                + "generated at insert"));
        IntStream.rangeClosed(0, 5)
                .forEach(j -> {
                    Map<String, Object> matrixParams = new HashMap<>();
                    matrixParams.put(I, 0);
                    matrixParams.put(J, j);
                    matrixParams.put(IS_CONDITION, true);
                    matrixParams.put(FLOAT_VALUE, Math.exp(j));
                    matrixParams.put(PROBLEM_ID, problemId);
                    final List<String> names
                            = new ArrayList(matrixParams.keySet());
                    final String tableInsert = "INSERT INTO " + MATRIX + "("
                            + names.stream().collect(Collectors.joining(", "))
                            + ") VALUES(:" + names.stream()
                                    .collect(Collectors.joining(", :")) + ")";
                    namedParameterJdbcTemplate.update(tableInsert, matrixParams);
                });
        return problemId;
    }

    private String toHexCodes(final String str) {
        final char[] chars = str.toCharArray();
        StringBuilder hexBuilder = new StringBuilder();
        for (char ch: chars) {
            int code = ch; //Character.getNumericValue(ch);
            String hexCh = Integer.toHexString(code);
            hexBuilder.append(hexCh);
        }
        return hexBuilder.toString();
    }

    @Transactional
    private Long createProblemWithSolution() {
        SqlUpdate problemIserter = new SqlUpdate(dataSource,
                "INSERT INTO " + PROBLEM + " (" + DESCRIPTION + ", "
                + KIND + ", " + PROBLEM_PRECISION + ", " + MATRIX_DIMENSION
                + ", " + IS_SOLVED
                + ") VALUES(:" + DESCRIPTION + ", :"
                + KIND + ", :" + PROBLEM_PRECISION + ", :" + MATRIX_DIMENSION
                + ", :" + IS_SOLVED + ")");
        problemIserter.declareParameter(new SqlParameter(DESCRIPTION, VARCHAR));
        problemIserter.declareParameter(new SqlParameter(KIND, VARCHAR));
        problemIserter.declareParameter(new SqlParameter(PROBLEM_PRECISION,
                NUMERIC));
        problemIserter.declareParameter(new SqlParameter(MATRIX_DIMENSION,
                NUMERIC));
        problemIserter.declareParameter(new SqlParameter(IS_SOLVED, BOOLEAN));

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(DESCRIPTION, toHexCodes(PROBLEM + DESCRIPTION));
        paramMap.put(KIND, Kind.POLYNOMIAL);
        paramMap.put(PROBLEM_PRECISION, 0);
        paramMap.put(MATRIX_DIMENSION, 1);
        paramMap.put(IS_SOLVED, true);
        problemIserter.setReturnGeneratedKeys(true);
        problemIserter.setGeneratedKeysColumnNames(ID);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        problemIserter.updateByNamedParam(paramMap, keyHolder);
        long problemId = Optional.ofNullable(keyHolder.getKey())
                .map(Number::longValue)
                .orElseThrow(() -> new RuntimeException("Problem id is not "
                + "generated at insert"));
        Map<String, Object> matrixParams = new HashMap<>();
        matrixParams.put(I, 0);
        matrixParams.put(J, 0);
        matrixParams.put(IS_CONDITION, true);
        matrixParams.put(FLOAT_VALUE, 1);
        matrixParams.put(PROBLEM_ID, problemId);
        final List<String> names
                = new ArrayList(matrixParams.keySet());
        final String tableInsert = "INSERT INTO " + MATRIX + "("
                + names.stream().collect(Collectors.joining(", "))
                + ") VALUES(:" + names.stream()
                        .collect(Collectors.joining(", :")) + ")";
        namedParameterJdbcTemplate.update(tableInsert, matrixParams);
        matrixParams.put(I, 0);
        matrixParams.put(J, 1);
        matrixParams.put(FLOAT_VALUE, -3);
        namedParameterJdbcTemplate.update(tableInsert, matrixParams);
        matrixParams.put(I, 0);
        matrixParams.put(J, 2);
        matrixParams.put(FLOAT_VALUE, 2);
        namedParameterJdbcTemplate.update(tableInsert, matrixParams);

        matrixParams.put(IS_CONDITION, false);
        matrixParams.put(I, 0);
        matrixParams.put(J, 0);
        matrixParams.put(FLOAT_VALUE, 1);
        namedParameterJdbcTemplate.update(tableInsert, matrixParams);
        matrixParams.put(I, 0);
        matrixParams.put(J, 1);
        matrixParams.put(FLOAT_VALUE, 2);
        namedParameterJdbcTemplate.update(tableInsert, matrixParams);
        return problemId;
    }

}
