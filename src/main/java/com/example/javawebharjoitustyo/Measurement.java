package com.example.javawebharjoitustyo;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
@Entity
@Table (name = "measurements")       
public class Measurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String type;
    @NotNull
    @Min(0) 
    private double value;

    @NotNull
    private LocalDateTime measurementDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person person;

    public Measurement() {}

    // Constructor with fields
    public Measurement(String type, double value, Person person) {
        this.type = type;
        this.value = value;
        this.person = person;
        this.measurementDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public double getValue() {
        return value;
    }

    public Person getPerson() {
        return person;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public LocalDateTime getMeasurementDate() {
        return measurementDate;
    }

    public void setMeasurementDate(LocalDateTime measurementDate) {
        this.measurementDate = measurementDate;
    }


}
