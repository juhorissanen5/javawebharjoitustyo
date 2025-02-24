package com.example.javawebharjoitustyo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create admin person if not exists
        if (personRepository.findByUsername("admin") == null) {
            Person admin = new Person("Admin User", "admin", passwordEncoder.encode("admin123"));
            admin.addRole("ADMIN");
            Address adminAddress = new Address("Admin Street 1", "Admin City");
            admin.setAddress(adminAddress);
            personRepository.save(admin);
        }

        // Create regular person if not exists
        if (personRepository.findByUsername("user") == null) {
            Person user = new Person("Regular User", "user", passwordEncoder.encode("user123"));
            user.addRole("USER");
            Address userAddress = new Address("User Street 123", "User City");
            user.setAddress(userAddress);

            personRepository.save(user);
        }
    }
}