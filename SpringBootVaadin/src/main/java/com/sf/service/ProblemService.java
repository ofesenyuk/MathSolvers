/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.service;

import com.sf.back.entities.Matrix;
import com.sf.back.entities.Problem;
import com.sf.repository.ProblemRepository;
import com.sf.converters.FieldsGenerator;
import com.sf.shared.dto.ProblemDTO;
import com.sf.shared.dto.MatrixRow;
import com.sf.shared.dto.ProblemMapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author sf
 */
@Service
//@RequiredArgsConstructor
public class ProblemService {
    private final ProblemRepository problemRepository;
    private final DozerBeanMapper mapper;
    private final FieldsGenerator fieldsGenerator;

    @Autowired
    public ProblemService(ProblemRepository problemRepository,
                          FieldsGenerator fieldsGenerator) {
        this.problemRepository = problemRepository;
        mapper = new DozerBeanMapper();
        this.fieldsGenerator = fieldsGenerator;
    }
    
    
    public List<ProblemDTO> findAll() {
        final List<Problem> problemsDb = problemRepository.findAll();
        final List<ProblemDTO> dtos = problemsDb.stream()
//                .map(problem -> mapper.map(problem, ProblemDTO.class))
                .map(this::toProblemDTO)
                .collect(Collectors.toList());
        return dtos;
    }
    
    private  ProblemDTO toProblemDTO(Problem problem) {
        ProblemDTO problemDTO = new DozerBeanMapper()
            .map(problem, ProblemDTO.class);
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
}
