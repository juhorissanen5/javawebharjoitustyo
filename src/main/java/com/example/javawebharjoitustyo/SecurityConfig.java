package com.example.javawebharjoitustyo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private PersonRepository personRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Person person = personRepository.findByUsername(username);
            if (person == null) {
                throw new UsernameNotFoundException("User not found");
            }
            return org.springframework.security.core.userdetails.User
                .withUsername(person.getUsername())
                .password(person.getPassword())
                .roles(person.getRoles().toArray(new String[0]))
                .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/favicon.ico").permitAll()  // Add favicon.ico
                .requestMatchers("/static/**").permitAll()    // Add static resources
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .permitAll()
            )
            .logout(logout -> logout
                .permitAll()
            );
    
        // Optional: If you need H2 Console during development
    
        return http.build();
    }
}