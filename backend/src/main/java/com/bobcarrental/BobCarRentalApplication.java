package com.bobcarrental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * BobCarRental - Modern Car Rental Management System
 * 
 * Main application class for the Spring Boot backend.
 * This application is a modernization of the legacy Alankar Travels
 * Clipper/Harbour application from 1995.
 * 
 * Features:
 * - RESTful API with JWT authentication
 * - Role-based access control (ADMIN/USER)
 * - H2 in-memory database
 * - Mobile-ready architecture
 * - Comprehensive validation and business logic
 * - Image upload with thumbnail generation
 * - PDF report generation
 * 
 * @author Bob
 * @version 1.0.0
 * @since 2026-03-26
 */
@SpringBootApplication
@EnableJpaAuditing
public class BobCarRentalApplication {

    public static void main(String[] args) {
        SpringApplication.run(BobCarRentalApplication.class, args);
        
        System.out.println("\n" +
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║                                                              ║\n" +
            "║              BobCarRental Backend Started!                   ║\n" +
            "║                                                              ║\n" +
            "║  API Documentation: http://localhost:8080/swagger-ui.html   ║\n" +
            "║  H2 Console:        http://localhost:8080/h2-console        ║\n" +
            "║  Health Check:      http://localhost:8080/actuator/health   ║\n" +
            "║                                                              ║\n" +
            "║  Default Users:                                              ║\n" +
            "║    - admin/admin (ADMIN role)                                ║\n" +
            "║    - user/user   (USER role)                                 ║\n" +
            "║                                                              ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n"
        );
    }
}

// Made with Bob
