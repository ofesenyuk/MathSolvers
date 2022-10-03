package com.sf.front;

import static com.sf.back.entities.Kind.POLYNOMIAL;
import com.sf.service.ProblemService;
import com.sf.shared.dto.ProblemDTO;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author sf
 */
@Route
public class MainView extends VerticalLayout {

    private static final String CONDITION_COL = "Condition";
    private static final String DESCRIPTION_COL = "Description";    
    
    Logger LOG = LoggerFactory.getLogger(MainView.class);

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
        
        add(grid, addNewBtn, editor);
        
        listProblems();
        // Connect selected Problem to editor or hide if none is selected
        grid.asSingleSelect().addValueChangeListener(e -> {
            editor.editProblem(e.getValue());
        });
        
        // Instantiate and edit new Problem the new button is clicked
        addNewBtn.addClickListener(e -> editor.createProblem());
        
        // Listen changes made by the editor, refresh data from backend
        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            listProblems();
        });
    }

    private void listProblems() {
        final List<ProblemDTO> problems = service.findAll();
        grid.setItems(problems);
        addNewColumnsIfAbsent();
        LOG.info("{} problems are read in front", problems.size());
    }

    private void addNewColumnsIfAbsent() {
        if (Arrays.asList(DESCRIPTION_COL, CONDITION_COL).stream()
                .map(key -> grid.getColumnByKey(key))
                .allMatch(Objects::isNull)) {
            grid.addColumn(ProblemDTO::getDescription).setHeader(DESCRIPTION_COL)
                    .setKey(DESCRIPTION_COL);
            grid.addComponentColumn(item -> createConditionGrid(item))
                    .setHeader(CONDITION_COL).setKey(CONDITION_COL);
        }
    }

    private HorizontalLayout createConditionGrid(ProblemDTO problemDTO) {
        final String[][] conditionArray = problemDTO.getConditionArray();
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
                verticalLayouts[j].add(label);
            }
        }
        return new HorizontalLayout(verticalLayouts);
    }    
}
