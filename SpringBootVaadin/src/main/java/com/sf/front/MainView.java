/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.front;

import com.sf.back.entities.Problem;
import com.sf.repository.ProblemRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

/**
 *
 * @author sf
 */
@Route
public class MainView extends VerticalLayout {

    private final ProblemRepository repo;
    final Grid<Problem> grid;

    public MainView(ProblemRepository repo) {
        this.repo = repo;
        this.grid = new Grid<>(Problem.class);
        add(grid);
        listProblems();
    }

    private void listProblems() {
        grid.setItems(repo.findAll());
    }
    
}
