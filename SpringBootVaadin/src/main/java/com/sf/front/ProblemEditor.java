/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.front;

import com.sf.back.entities.Kind;
import com.sf.service.ProblemService;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author sf
 */
@SpringComponent
@UIScope
public class ProblemEditor extends VerticalLayout implements KeyNotifier {
    private final ProblemService service;
    Button save = new Button("Save", VaadinIcon.CHECK.create());
    Button cancel = new Button("Cancel");
    Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);
    TextArea description = new TextArea("description");
    ComboBox<Kind> kinds = new ComboBox<>("Select problem kind", Kind.values());
    FormLayout conditionLayout = new FormLayout(kinds);

    @Autowired
    public ProblemEditor(ProblemService service) {
        this.service = service;
        add(description, conditionLayout, actions);
    }

    public void editProblem() {
        setVisible(true);
    }
}
