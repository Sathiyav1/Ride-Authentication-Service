package com.chalow.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.chalow.auth.entity")
@EnableJpaRepositories(basePackages = "com.chalow.auth.repository")
public class RideAuthenticationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RideAuthenticationServiceApplication.class, args);
    }
}
