package com.fiddich.review;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ReViewApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReViewApplication.class, args);
    }
}
