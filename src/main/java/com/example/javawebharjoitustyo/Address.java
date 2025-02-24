package com.example.javawebharjoitustyo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    private String street;
    
    @NotNull
    private String city;
    
    @OneToOne(mappedBy = "address")
    private Person person;
    
    // Constructors
    public Address() {}
    
    public Address(String street, String city) {
        this.street = street;
        this.city = city;
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public String getStreet() {
        return street;
    }
    
    public String getCity() {
        return city;
    }
    
    public Person getPerson() {
        return person;
    }
    
    // Setters
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setStreet(String street) {
        this.street = street;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public void setPerson(Person person) {
        this.person = person;
    }
    
    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}