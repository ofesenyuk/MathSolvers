/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.shared.dto;

import com.sf.back.entities.Problem;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.factory.Mappers;

/**
 *
 * @author sf
 */
//@Mapper
public interface ProblemMapper {
//    ProblemMapper MAPPER = Mappers.getMapper(ProblemMapper.class);
//    @Mapping(source = "")
    ProblemDTO toDto(Problem problem);
}
