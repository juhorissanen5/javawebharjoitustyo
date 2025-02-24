package com.example.javawebharjoitustyo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
@Route("admin")
@Secured("ADMIN")
@PageTitle("Health Monitoring")
@CssImport("../frontend/custom-styles.css")
public class MainView extends VerticalLayout {
    private final PersonRepository personRepository;
    private final MeasurementRepository measurementRepository;
    private final PasswordEncoder passwordEncoder;
    private Grid<Person> personGrid = new Grid<>(Person.class);
    private Grid<Measurement> measurementGrid = new Grid<>(Measurement.class);
    private TextField nameFilter = new TextField();
    private TextField typeFilter = new TextField();
    private NumberField valueFilter = new NumberField();
    private TextField addressFilter = new TextField();
    private DateTimePicker dateFromFilter = new DateTimePicker();
    private DateTimePicker dateToFilter = new DateTimePicker();

    public MainView(PersonRepository personRepository, MeasurementRepository measurementRepository, PasswordEncoder passwordEncoder, UpdateService updateService) {
        this.personRepository = personRepository;
        this.measurementRepository = measurementRepository;
        this.passwordEncoder = passwordEncoder;
        updateService.setMainView(this);

        // Add some Lumo utility classes
        addClassName("spacing-xl");
        addClassName("p-l");

        H1 title = new H1("Health Monitoring System");
        title.addClassName("text-xl");
        title.getStyle().set("color", "var(--lumo-primary-color)");

        // Style grids directly
        personGrid.addClassName("custom-grid");
        personGrid.getStyle()
            .set("border", "1px solid var(--lumo-contrast-20pct)")
            .set("border-radius", "var(--lumo-border-radius-l)");

        measurementGrid.addClassName("custom-grid");
        measurementGrid.getStyle()
            .set("border", "1px solid var(--lumo-contrast-20pct)")
            .set("border-radius", "var(--lumo-border-radius-l)");

        configureGrids();
        add(title, createToolbar(), new H2("Registered Persons"),
            personGrid,
            new H2("Health Measurements"),
            measurementGrid, createFooter());
        updateLists();
    }
    private Component createFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.addClassName("footer");
        footer.setWidthFull();
        footer.setJustifyContentMode(JustifyContentMode.BETWEEN);
        
        VerticalLayout leftSection = new VerticalLayout();
        leftSection.add(new Span("© 2025 Health Monitoring System"));
        leftSection.add(new Span("All rights reserved"));
    
        VerticalLayout centerSection = new VerticalLayout();
        Button contactUs = new Button("Contact Us", e -> getUI().ifPresent(ui -> ui.navigate("contact")));
        Button about = new Button("About", e -> getUI().ifPresent(ui -> ui.navigate("about")));
        centerSection.add(contactUs, about);
    
        VerticalLayout rightSection = new VerticalLayout();
        rightSection.add(new Span("Follow us:"));
        HorizontalLayout socialLinks = new HorizontalLayout();
        socialLinks.add(
            new Anchor("https://twitter.com", "Twitter"),
            new Anchor("https://facebook.com", "Facebook")
        );
        rightSection.add(socialLinks);
    
        footer.add(leftSection, centerSection, rightSection);
        return footer;
    } 
    public void pushUpdate() {
        getUI().ifPresent(ui -> ui.access(() -> {
            updateLists();
            ui.push();
        }));
    } 
private void configureGrids() {
    // Configure filters
    nameFilter.setPlaceholder("Filter by name...");
    typeFilter.setPlaceholder("Filter by type...");
    valueFilter.setPlaceholder("Filter by value...");
    addressFilter.setPlaceholder("Filter by address...");
    dateFromFilter.setLabel("From date");
    dateToFilter.setLabel("To date");

    nameFilter.addClassName("filter-field");
    typeFilter.addClassName("filter-field");
    valueFilter.addClassName("filter-field");
    addressFilter.addClassName("filter-field");
    dateFromFilter.addClassName("filter-field");
    dateToFilter.addClassName("filter-field");
    Button applyFilters = new Button("Apply Filters", e -> updateList());
    Button clearFilters = new Button("Clear Filters", e -> clearFilters());

    applyFilters.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    clearFilters.addThemeVariants(ButtonVariant.LUMO_ERROR);
    // Add filter listeners
    nameFilter.addValueChangeListener(e -> updateList());
    typeFilter.addValueChangeListener(e -> updateList());
    valueFilter.addValueChangeListener(e -> updateList());
    addressFilter.addValueChangeListener(e -> updateList());
    dateFromFilter.addValueChangeListener(e -> updateList());
    dateToFilter.addValueChangeListener(e -> updateList());

    // Original grid configuration
    personGrid.removeAllColumns();
    personGrid.addColumn(Person::getName).setHeader("Name");
    personGrid.addColumn(person -> person.getAddress() != null ? 
        person.getAddress().getStreet() + ", " + person.getAddress().getCity() : "No address")
        .setHeader("Address");
    personGrid.addComponentColumn(this::createPersonActions).setHeader("Actions");
    
    measurementGrid.removeAllColumns();
    measurementGrid.addColumn(m -> m.getPerson().getName()).setHeader("Person");
    measurementGrid.addColumn(Measurement::getType).setHeader("Type");
    measurementGrid.addColumn(Measurement::getValue).setHeader("Value");
    measurementGrid.addColumn(Measurement::getMeasurementDate).setHeader("Date");
    measurementGrid.addComponentColumn(this::createMeasurementActions).setHeader("Actions");

    // Add filter toolbars
    HorizontalLayout personFilters = new HorizontalLayout(
        nameFilter,
        addressFilter
    );
    personFilters.addClassName("filter-container");

    HorizontalLayout measurementFilters = new HorizontalLayout(
        typeFilter,
        valueFilter,
        dateFromFilter,
        dateToFilter
    );
    measurementFilters.addClassName("filter-container");


    HorizontalLayout filterButtons = new HorizontalLayout(applyFilters, clearFilters);
    filterButtons.addClassName("filter-buttons");

    add(new H2("Person Filters"), personFilters,
        new H2("Measurement Filters"), measurementFilters,
        filterButtons);
}
private void clearFilters() {
    nameFilter.clear();
    typeFilter.clear();
    valueFilter.clear();
    addressFilter.clear();
    dateFromFilter.clear();
    dateToFilter.clear();
    updateList();
    Notification.show("Filters cleared");
}
    private void deletePerson(Person person) {
        // First, delete all measurements associated with this person
        measurementRepository.deleteAll(measurementRepository.findByPerson(person));
        
        // Then delete the person
        personRepository.delete(person);
        updateLists();
        Notification.show("Person and associated measurements deleted");
    }
    private Component createPersonActions(Person person) {
        Button edit = new Button("Edit");
        Button delete = new Button("Delete");
        
        edit.addClickListener(e -> editPerson(person));
        delete.addClickListener(e -> deletePerson(person));
        
        return new HorizontalLayout(edit, delete);
    }
    
    private Component createMeasurementActions(Measurement measurement) {
        Button edit = new Button("Edit");
        Button delete = new Button("Delete");
        
        edit.addClickListener(e -> editMeasurement(measurement));
        delete.addClickListener(e -> deleteMeasurement(measurement));
        
        return new HorizontalLayout(edit, delete);
    }
    
    private Component createToolbar() {
        Button addPerson = new Button("Add Person");
        Button addMeasurement = new Button("Add Measurement");
        Button viewMyMeasurements = new Button("View My Measurements");

        Button logoutButton = new Button("Logout", e -> {
            SecurityContextHolder.clearContext();
            getUI().ifPresent(ui -> ui.getPage().setLocation("/logout"));
        });


        // Style buttons using Lumo utility classes
        addPerson.addClassNames("success", "primary");
        addMeasurement.addClassNames("success", "primary");
        viewMyMeasurements.addClassNames("contrast", "primary");
        logoutButton.addClassNames("error", "primary");

        viewMyMeasurements.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate(""));
        });
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.addClassName("toolbar");
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        HorizontalLayout addButtons = new HorizontalLayout(addPerson, addMeasurement, viewMyMeasurements);
        toolbar.add(addButtons, logoutButton);
        addPerson.addClickListener(e -> addPerson());
        addMeasurement.addClickListener(e -> addMeasurement());
        
        return toolbar;
    }
    
    private Component createGridLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.add(personGrid, measurementGrid);
        return layout;
    }
    private void updateList() {
    // Filter persons
    List<Person> filteredPersons = personRepository.findAll().stream()
        .filter(person -> nameFilter.getValue().isEmpty() || 
            person.getName().toLowerCase().contains(nameFilter.getValue().toLowerCase()))
        .filter(person -> addressFilter.getValue().isEmpty() || 
            (person.getAddress() != null && 
            (person.getAddress().getStreet().toLowerCase().contains(addressFilter.getValue().toLowerCase()) ||
             person.getAddress().getCity().toLowerCase().contains(addressFilter.getValue().toLowerCase()))))
        .collect(Collectors.toList());
    
    personGrid.setItems(filteredPersons);

    // Filter measurements
    List<Measurement> filteredMeasurements = measurementRepository.findAll().stream()
        .filter(measurement -> typeFilter.getValue().isEmpty() || 
            measurement.getType().toLowerCase().contains(typeFilter.getValue().toLowerCase()))
        .filter(measurement -> valueFilter.isEmpty() || 
            measurement.getValue() == valueFilter.getValue())
        .filter(measurement -> dateFromFilter.isEmpty() || 
            measurement.getMeasurementDate().isAfter(dateFromFilter.getValue()))
        .filter(measurement -> dateToFilter.isEmpty() || 
            measurement.getMeasurementDate().isBefore(dateToFilter.getValue()))
        .collect(Collectors.toList());
    
    measurementGrid.setItems(filteredMeasurements);
}
private void updateLists() {
    List<Person> persons = personRepository.findAll();
    List<Measurement> measurements = measurementRepository.findAll();
    
    getUI().ifPresent(ui -> ui.access(() -> {
        personGrid.setItems(persons);
        measurementGrid.setItems(measurements);
        ui.push();
    }));
}
    
    private void addPerson() {
        Dialog dialog = new Dialog();
        TextField nameField = new TextField("Name");
        TextField usernameField = new TextField("Username");
        TextField passwordField = new TextField("Password"); // In real app, use PasswordField
        
        TextField streetField = new TextField("Street Address");
        TextField cityField = new TextField("City");
        nameField.setRequired(true);
        usernameField.setRequired(true);
        passwordField.setRequired(true);
        streetField.setRequired(true);
        cityField.setRequired(true);
        Button save = new Button("Save", e -> {
            if (nameField.getValue().isEmpty() || 
            usernameField.getValue().isEmpty() || 
            passwordField.getValue().isEmpty() ||
            streetField.getValue().isEmpty() ||
            cityField.getValue().isEmpty()) {
            Notification.show("All fields are required");
            return;
        }
            
            Person person = new Person(
                nameField.getValue(),
                usernameField.getValue(),
                passwordEncoder.encode(passwordField.getValue())
            );
            Address address = new Address(streetField.getValue(), cityField.getValue());
            person.setAddress(address);
            try {
                personRepository.save(person);
                dialog.close();
                updateLists();
                Notification.show("Person added successfully");
            } catch (Exception ex) {
                Notification.show("Error: Username may already exist");
            }
        });
    
        Button cancel = new Button("Cancel", e -> dialog.close());
        
        dialog.add(new VerticalLayout(
            nameField, 
            usernameField,
            passwordField,
            streetField,
            cityField,
            new HorizontalLayout(save, cancel)
        ));
        dialog.open();
    }
    
    private void editPerson(Person person) {
        Dialog dialog = new Dialog();
        TextField nameField = new TextField("Name", person.getName());
        TextField usernameField = new TextField("Username", person.getUsername());
        TextField passwordField = new TextField("Password");
        passwordField.setPlaceholder("Enter new password (leave empty to keep current)");
        
        // Add address fields
        TextField streetField = new TextField("Street Address");
        TextField cityField = new TextField("City");
        
        // Set existing address if available
        if (person.getAddress() != null) {
            streetField.setValue(person.getAddress().getStreet());
            cityField.setValue(person.getAddress().getCity());
        }
        
        nameField.setRequired(true);
        usernameField.setRequired(true);
        streetField.setRequired(true);
        cityField.setRequired(true);
    
        Button save = new Button("Save", e -> {
            if (nameField.getValue().isEmpty() || 
                usernameField.getValue().isEmpty() ||
                streetField.getValue().isEmpty() ||
                cityField.getValue().isEmpty()) {
                Notification.show("Name, username, and address fields are required");
                return;
            }
    
            person.setName(nameField.getValue());
            person.setUsername(usernameField.getValue());
            if (!passwordField.getValue().isEmpty()) {
                person.setPassword(passwordEncoder.encode(passwordField.getValue())); 
            }
            
            // Update or create address
            Address address = person.getAddress();
            if (address == null) {
                address = new Address();
            }
            address.setStreet(streetField.getValue());
            address.setCity(cityField.getValue());
            person.setAddress(address);
    
            try {
                personRepository.save(person);
                dialog.close();
                updateLists();
                Notification.show("Person updated successfully");
            } catch (Exception ex) {
                Notification.show("Error: Username may already exist");
            }
        });
    
        Button cancel = new Button("Cancel", e -> dialog.close());
    
        dialog.add(new VerticalLayout(
            nameField,
            usernameField,
            passwordField,
            streetField,
            cityField,
            new HorizontalLayout(save, cancel)
        ));
        dialog.open();
    }
    
    private void addMeasurement() {
        Dialog dialog = new Dialog();
        ComboBox<Person> personSelect = new ComboBox<>("Person");
        personSelect.setItems(personRepository.findAll());
        personSelect.setItemLabelGenerator(Person::getName);
        
        TextField typeField = new TextField("Type");
        NumberField valueField = new NumberField("Value");
        DateTimePicker dateTimePicker = new DateTimePicker("Date and Time", LocalDateTime.now());
        
        Button save = new Button("Save", e -> {
            Measurement measurement = new Measurement(
                typeField.getValue(),
                valueField.getValue(),
                personSelect.getValue()
            );
            measurement.setMeasurementDate(dateTimePicker.getValue());
            measurementRepository.save(measurement);
            dialog.close();
            updateLists();
            Notification.show("Measurement added");
        });
        Button cancel = new Button("Cancel", e -> dialog.close());
        HorizontalLayout buttons = new HorizontalLayout(save, cancel);

        dialog.add(new VerticalLayout(personSelect, typeField, valueField, dateTimePicker, buttons));
        dialog.open();
    }
    
    private void editMeasurement(Measurement measurement) {
        Dialog dialog = new Dialog();
        ComboBox<Person> personSelect = new ComboBox<>("Person");
        personSelect.setItems(personRepository.findAll());
        personSelect.setItemLabelGenerator(Person::getName);
        personSelect.setValue(measurement.getPerson());
        
        TextField typeField = new TextField("Type", measurement.getType());
        NumberField valueField = new NumberField("Value");
        valueField.setValue(measurement.getValue());
        DateTimePicker dateTimePicker = new DateTimePicker("Date and Time", measurement.getMeasurementDate());
        
        Button save = new Button("Save", e -> {
            measurement.setPerson(personSelect.getValue());
            measurement.setType(typeField.getValue());
            measurement.setValue(valueField.getValue());
            measurement.setMeasurementDate(dateTimePicker.getValue());
            measurementRepository.save(measurement);
            dialog.close();
            updateLists();
            Notification.show("Measurement updated");
        });
        
        dialog.add(new VerticalLayout(personSelect, typeField, valueField, dateTimePicker, save));
        dialog.open();
    }
    
    private void deleteMeasurement(Measurement measurement) {
        measurementRepository.delete(measurement);
        updateLists();
        Notification.show("Measurement deleted");
    }
}