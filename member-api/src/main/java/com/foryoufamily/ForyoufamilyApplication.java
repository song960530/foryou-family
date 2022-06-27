package com.foryoufamily;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ForyoufamilyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForyoufamilyApplication.class, args);
    }

}
