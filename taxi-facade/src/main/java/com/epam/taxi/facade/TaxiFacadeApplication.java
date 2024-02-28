package com.epam.taxi.facade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.epam.taxi")
public class TaxiFacadeApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaxiFacadeApplication.class, args);
    }
}
