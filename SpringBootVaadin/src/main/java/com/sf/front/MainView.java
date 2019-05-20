/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.front;

import static com.sf.back.entities.Kind.POLYNOMIAL;
import com.sf.back.entities.Problem;
import com.sf.repository.ProblemRepository;
import com.sf.service.ProblemService;
import com.sf.shared.dto.MatrixRow;
import com.sf.shared.dto.ProblemDTO;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author sf
 */
@Route
public class MainView extends VerticalLayout {

    private final ProblemService service;
    
    private final ProblemEditor problemEditor;
    final Grid<ProblemDTO> grid;
    private final Button addNewBtn;

    @Autowired
    public MainView(ProblemService service, ProblemEditor editor) {
        this.service = service;
        this.grid = new Grid<>();
        this.addNewBtn = new Button("New problem", VaadinIcon.PLUS.create());
        this.problemEditor = editor;
        
        add(grid, addNewBtn);
        
        listProblems();
        addNewBtn.addClickListener(e -> editor.editProblem());
    }

    private void listProblems() {
        final List<ProblemDTO> problems = service.findAll();
        grid.setItems(problems);
        grid.addColumn(ProblemDTO::getDescription).setHeader("Description");
        grid.addComponentColumn(item -> createMatrixGrid(item))
                .setHeader("Condition");
//        grid.setColumns("Description", "Condition", "Solution");
//        grid.addComponentColumn(ProblemDTO::new).setHeader("dssdfgasf");
    }

    private HorizontalLayout createMatrixGrid(ProblemDTO problemDTO) {
        final Grid<MatrixRow> matrixGrid = new Grid<>(MatrixRow.class);
        final String[][] conditionArray = problemDTO.getConditionArray();
//        matrixGrid.setItems(item.getCondition());
//        System.out.println("item 0 = " + item.getCondition().get(0).getMatrixRow().get(0));
//        System.out.println("\nitem 0 = " + item.getCondition().get(0).getMetaClass().getJ1());
//        matrixGrid.addColumn(MatrixRow::getMatrixRow).setHeader("");
        VerticalLayout[] verticalLayouts 
                = new VerticalLayout[conditionArray[0].length];
        for (int j = 0; j < conditionArray[0].length; j++) {
            verticalLayouts[j] = new VerticalLayout();
        }
        for (int i = 0; i < conditionArray.length; i++) {
            for (int j = 0; j < conditionArray[i].length; j++) {
                String value = conditionArray[i][j];
                if (problemDTO.getKind().equals(POLYNOMIAL) && j > 0) {
                    value += "x" + (j > 1 ? "^" + j : "");
                }
                final Label label = new Label(value);
//                matrixGrid.addComponentColumn(item -> new MatrixCell(value));
                verticalLayouts[j].add(label);
            }
        }
        return new HorizontalLayout(verticalLayouts);
    }
    

//    private Grid<MatrixRow> createMatrixGrid(ProblemDTO problemDTO) {
//        final Grid<MatrixRow> matrixGrid = new Grid<>(MatrixRow.class);
//        final String[][] conditionArray = problemDTO.getConditionArray();
//        matrixGrid.setItems(problemDTO.getCondition());
////        System.out.println("item 0 = " + item.getCondition().get(0).getMatrixRow().get(0));
////        System.out.println("\nitem 0 = " + item.getCondition().get(0).getMetaClass().getJ1());
////        matrixGrid.addColumn(MatrixRow::getMatrixRow).setHeader("");
////        for (int i = 0; i < conditionArray.length; i++) {
////            for (int j = 0; j < conditionArray[i].length; j++) {
////                final String value = conditionArray[i][j];
////                final Label label = new Label(value);
////                matrixGrid.addComponentColumn(item -> MatrixCell(item));
////            }
////        }
//        matrixGrid.addComponentColumn(item -> new MatrixCell(item));
////        matrixGrid.addComponentColumn(componentProvider)
//        return matrixGrid;
//    }
    
    static class MatrixCell extends Div {
        private final String text;

        public MatrixCell(String text) {
            this.text = text;
            setText(text);
        }

        private MatrixCell(MatrixRow row) {
            text = row.getCells().get(0);
            setText(text);            
        }
    }
    
}
