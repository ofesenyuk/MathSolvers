package com.sf;

import com.sf.back.entities.Kind;
import com.sf.back.entities.Matrix;
import com.sf.back.entities.Problem;
import com.sf.service.ProblemService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class VaadinMathSolverApplication {

    Logger LOG = LoggerFactory.getLogger(VaadinMathSolverApplication.class);

    public static void main(String[] args) {
            SpringApplication.run(VaadinMathSolverApplication.class, args);
    }

    /**
     * Test data preparation
     * @param problemService
     * @return 
     */
    @Bean
    public CommandLineRunner loadData(ProblemService problemService) {
        return (args) -> {
            final Problem p = new Problem();
            p.setDescription("Find all roots of given polynomial".getBytes());
            p.setIsSolved(false);
            p.setKind(Kind.POLYNOMIAL);
            p.setMatrixDimension(1);
            p.setProblemPrecision(6);
            final Matrix a = new Matrix();
            a.setI(0);
            a.setJ(0);
            a.setFloatValue(1.0d);
            a.setIsCondition(true);
            a.setParentProblem(p);
            final Matrix b = new Matrix();
            b.setI(0);
            b.setJ(1);
            b.setFloatValue(2.0d);
            b.setIsCondition(true);
            b.setParentProblem(p);
            final Matrix c = new Matrix();
            c.setI(0);
            c.setJ(2);
            c.setFloatValue(1.0d);
            c.setIsCondition(true);
            c.setParentProblem(p);
            final List<Matrix> coefficients = Arrays.asList(a, b, c);
            
            final Matrix x1 = new Matrix();
            x1.setI(0);
            x1.setJ(0);
            x1.setFloatValue(1.0);
            x1.setIsCondition(false);  
            x1.setParentProblem(p);
            final Matrix x2 = new Matrix();
            x2.setI(0);
            x2.setJ(1);
            x2.setFloatValue(1.0);
            x2.setIsCondition(false);
            x2.setParentProblem(p);
            final List<Matrix> solutions = Arrays.asList(x1, x2);
            
            final List<Matrix> matrixes = new ArrayList<>(coefficients);
            matrixes.addAll(solutions);
            
            p.setMatrixes(matrixes);
            problemService.save(p);
            LOG.info("test data are prepared and saved");
        };
    }
}
