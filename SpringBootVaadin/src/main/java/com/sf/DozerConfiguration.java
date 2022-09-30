/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sf;

import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import com.github.dozermapper.spring.DozerBeanMapperFactoryBean;
import com.sf.back.entities.Problem;
import com.sf.shared.dto.ProblemDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author OFeseniuk
 */
@Configuration
public class DozerConfiguration {
    
//    @Bean
//    public DozerBeanMapperFactoryBean dozerBeanMapperFactoryBean() {
//        final DozerBeanMapperFactoryBean dozerBeanMapperFactoryBean 
//                = new DozerBeanMapperFactoryBean();
//        dozerBeanMapperFactoryBean.setCustomFieldMapper(customFieldMapper);
//    }
    
    @Bean
    public BeanMappingBuilder beanMappingBuilder() {
        return new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(Problem.class, ProblemDTO.class)
                        .exclude("description");
            }
        };
    }
}
