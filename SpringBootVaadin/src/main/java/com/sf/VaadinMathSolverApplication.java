package com.sf;

import com.sf.back.entities.Kind;
import com.sf.back.entities.Matrix;
import com.sf.back.entities.Problem;
import com.sf.repository.MatrixRepository;
import com.sf.repository.ProblemRepository;
import java.util.Arrays;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class VaadinMathSolverApplication {

    public static void main(String[] args) {
            SpringApplication.run(VaadinMathSolverApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(ProblemRepository problemRepository, 
                                      MatrixRepository matrixRepository) {
        return (args) -> {
            final Problem p = new Problem();
            p.setDescription("Find all roots of given polynomial".getBytes());
            p.setIsSolved(false);
            p.setKind(Kind.POLYNOMIAL);
            p.setMatrixDimension(1);
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
            System.out.println("data are prepared");
            final List<Matrix> coefficients = Arrays.asList(a, b, c);
//            coefficients.forEach(coeff -> matrixRepository.save(coeff));
            
            p.setMatrixes(coefficients);
            problemRepository.save(p);
        };
    }
}
