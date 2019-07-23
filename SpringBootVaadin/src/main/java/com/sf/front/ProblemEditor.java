package com.sf.front;

import com.sf.back.entities.Kind;
import com.sf.service.ProblemService;
import com.sf.shared.dto.ProblemDTO;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author sf
 */
@SpringComponent
@UIScope
public class ProblemEditor extends VerticalLayout implements KeyNotifier {
    private final ProblemService service;
    
    /**
     * The currently edited problem
     */
    private ProblemDTO problem;
    
    Button save = new Button("Save", VaadinIcon.CHECK.create());
    Button cancel = new Button("Cancel");
    Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    
    HorizontalLayout solutionLayout = new HorizontalLayout();
    Details solutionButton = new Details("Solution", solutionLayout);
    
    HorizontalLayout actions = new HorizontalLayout(save, cancel, delete, 
        solutionButton);
    HorizontalLayout matrixInput = new HorizontalLayout();
    
    TextArea description = new TextArea("description");
    ComboBox<Kind> kind = new ComboBox<>("Select problem kind", Kind.values());
    TextField dimension = new TextField("Polynomial order");
    TextField problemPrecision = new TextField("Problem precision");
    HorizontalLayout matrix = new HorizontalLayout();
    
    FormLayout conditionLayout = new FormLayout(description, kind, dimension, 
            problemPrecision);
    Binder<ProblemDTO> binder = new Binder<>(ProblemDTO.class);
    private ChangeHandler changeHandler;

    @Autowired
    public ProblemEditor(ProblemService service) {
        this.service = service;
        add(conditionLayout, matrixInput, actions);
        
        // bind using naming convention
        binder.bindInstanceFields(this);

        // Configure and style components
        setSpacing(true);

        save.getElement().getThemeList().add("primary");
        delete.getElement().getThemeList().add("error");
        kind.setRequired(true);
        kind.setValue(Kind.POLYNOMIAL);

//        addKeyPressListener(Key.ENTER, e -> save());

        // wire action buttons to save, delete and reset
        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> editProblem(problem));
        
        solutionButton.addThemeVariants(DetailsVariant.REVERSE, 
                                        DetailsVariant.FILLED);
        solutionButton.addOpenedChangeListener(this::sendSolutionRequest);
        
        kind.addValueChangeListener(this::onKindSelect);
        dimension.addValueChangeListener(this::onDimensionChange);
        
        setVisible(false);
    }
    
    void delete() {
        service.delete(problem);
        changeHandler.onChange();
    }

    void save() {
        readProblemFromInput();
        readDtoFromMatrixInput();
        service.save(problem);
//        problem = null;
        changeHandler.onChange();
    }

    public void createProblem() {
        problem = new ProblemDTO();
        problem.setKind(kind.getValue());
        setVisible(true);
    }

    void editProblem(ProblemDTO value) {
        if (value == null) {
            setVisible(false);
            return;
        }
        final boolean isPersisted = value.getId() != null;
        if (isPersisted) {
            // Find fresh entity for editing
            problem = service.findById(value.getId());
        } else {
            problem = value;
        }
        cancel.setVisible(isPersisted);

        // Bind problem properties to similarly named fields
        // Could also use annotation or "manual binding" or programmatically
        // moving values from fields to entities before saving
        binder.setBean(problem);

        setVisible(true);

        // Focus first name initially
        description.focus();
        fillMatrixInputFromDto();
        final String[][] conditionArray = problem.getConditionArray();
        
        StringBuilder dim = new StringBuilder();
        if (conditionArray.length > 1) {
            dim.append(conditionArray.length).append("X");
        }
        dim.append(conditionArray[0].length - 1);
        dimension.setValue(dim.toString());
    }
    
    public void setChangeHandler(ChangeHandler h) {
        // ChangeHandler is notified when either save or delete
        // is clicked
        changeHandler = h;
    }

    private void fillMatrixInputFromDto() {
        if (problem == null) {
            return;
        }
        final String[][] conditionArray = problem.getConditionArray();
        if (conditionArray == null) {
            return;
        }
        final int nRows = conditionArray.length;
        final int nCols = conditionArray[0].length;
        VerticalLayout[] matrixInputCols = new VerticalLayout[nCols];
        for (int j = 0; j < nCols; j++) {
            matrixInputCols[j] = new VerticalLayout();
        }
        matrixInput.removeAll();
        matrixInput.add(matrixInputCols);
        for (int i = 0; i < nRows; i++) {
            String[] row = conditionArray[i];
            String rowNo = nRows > 1 ? String.valueOf(i)  + " " : "";
            for (int j = 0; j < row.length; j++) {
                final TextField cell = new TextField();
                final String colNo = String.valueOf(j);
                cell.setLabel(rowNo + colNo);
                cell.setValue(row[j]);
                matrixInputCols[j].add(cell);
            }
        }
    }

    private void readDtoFromMatrixInput() {
        final List<Component> cols = matrixInput.getChildren()
            .collect(Collectors.toList());
        if (cols.isEmpty()) {
            return;
        }
        final int nRows = Long.valueOf(cols.get(0).getChildren()
                .filter(component -> component instanceof TextField)
                .count())
            .intValue();
        problem.setConditionArray(new String[nRows][cols.size()]);
                
        for (int j = 0; j < cols.size(); j++) {
            final Component col = cols.get(j);
            final List<TextField> cells 
                = col.getChildren()
                .filter(component -> component instanceof TextField)
                .map(component -> (TextField)component)
                .collect(Collectors.toList());
            for (int i = 0; i < cells.size(); i++) {
                problem.getConditionArray()[i][j] = cells.get(i).getValue();
            }
        }
   }

    private void onKindSelect(
            AbstractField.ComponentValueChangeEvent<ComboBox<Kind>, Kind> event
    ) {
        if (event.getValue() == null) {
            return;
        }
        if (event.getValue().equals(Kind.POLYNOMIAL)) {
            dimension.setLabel("Polynomial order");
        }
        if (problem != null && problem.getConditionArray() != null 
                && problem.getConditionArray().length > 0) {
            dimension.setValue(String
                    .valueOf(problem.getConditionArray()[0].length - 1));
        }
    }

    private void onDimensionChange(
            AbstractField.ComponentValueChangeEvent<TextField, String> event
    ) {
        int iMax = 0, jMax = 0;
        if (kind.getValue().equals(Kind.POLYNOMIAL)) {
            iMax = 0;
            jMax = Integer.parseInt(dimension.getValue());
        }
        String[][] newMatrix = new String[iMax + 1][jMax +1];
        for (int i = 0; i <= iMax; i++) {
            for (int j = 0; j <= jMax; j++) {
                if (problem != null && problem.getConditionArray() != null
                    && i < problem.getConditionArray().length
                    && j < problem.getConditionArray()[i].length) {
                    newMatrix[i][j] = problem.getConditionArray()[i][j];
                } else {
                    newMatrix[i][j] = "0";
                }
            }
        }
        problem.setConditionArray(newMatrix);
        fillMatrixInputFromDto();
    }

    private void readProblemFromInput() {
        problem.setDescription(description.getValue());
    }

    private Registration sendSolutionRequest(Details.OpenedChangeEvent e) {
        if (e.isOpened()) {
            problem.setSolution(service.getSolution(problem.getId()));
        }
        return null;
    }
    
    public interface ChangeHandler {
        void onChange();
    }
}
