package com.example.javawebharjoitustyo;

import java.util.Optional;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("/")
@Secured({"USER", "ADMIN"}) // Allow both USER and ADMIN roles
@PageTitle("My Health Measurements")
public class UserView extends VerticalLayout {
    private final MeasurementRepository measurementRepository;
    private final PersonRepository personRepository;
    private Grid<Measurement> measurementGrid = new Grid<>(Measurement.class);
    
    public UserView(MeasurementRepository measurementRepository, PersonRepository personRepository) {
        if (measurementRepository == null || personRepository == null) {
            throw new IllegalArgumentException("Repositories cannot be null");
        }
        
        this.measurementRepository = measurementRepository;
        this.personRepository = personRepository;
        
        setSizeFull();
        configureGrid();
        add(
            new H1("My Health Measurements"),
            createToolbar(),
            measurementGrid
        );
        
        try {
            updateList();
        } catch (Exception e) {
            Notification.show("Error loading measurements: " + e.getMessage());
        }
    }
    
    private void configureGrid() {
        measurementGrid.setSizeFull();
        measurementGrid.removeAllColumns();
        measurementGrid.addColumn(Measurement::getType).setHeader("Type").setSortable(true);
        measurementGrid.addColumn(Measurement::getValue).setHeader("Value").setSortable(true);
        measurementGrid.addColumn(Measurement::getMeasurementDate).setHeader("Date").setSortable(true);
    }
    
    private Component createToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.END);
    
        // Add admin button if user has admin role
        if (hasRole("ADMIN")) {
            Button adminButton = new Button("Admin View", e -> 
                getUI().ifPresent(ui -> ui.navigate("admin")));
            toolbar.add(adminButton);
        }
    
        Button logoutButton = new Button("Logout", e -> {
            SecurityContextHolder.clearContext();
            getUI().ifPresent(ui -> ui.getPage().setLocation("/logout"));
        });
        
        toolbar.add(logoutButton);
        return toolbar;
    }

    private boolean hasRole(String role) {
        return Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .map(auth -> auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_" + role)))
            .orElse(false);
    }
    
    private void updateList() {
        Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .map(auth -> auth.getName())
            .ifPresent(username -> {
                Person currentPerson = personRepository.findByUsername(username);
                if (currentPerson != null) {
                    measurementGrid.setItems(measurementRepository.findByPerson(currentPerson));
                } else {
                    Notification.show("User not found");
                }
            });
    }
}