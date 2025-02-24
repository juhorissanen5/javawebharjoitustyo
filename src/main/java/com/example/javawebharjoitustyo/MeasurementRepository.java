package com.example.javawebharjoitustyo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
    List<Measurement> findByTypeContainingIgnoreCase(String type);
    List<Measurement> findByValueBetween(Double min, Double max);
    List<Measurement> findByMeasurementDateBetween(LocalDateTime start, LocalDateTime end);
    List<Measurement> findByPersonNameContainingIgnoreCase(String name);
    List<Measurement> findByPerson(Person person);
}
