package com.epam.taxi.logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.epam.taxi")
public class TaxiLoggerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaxiLoggerApplication.class, args);
    }
}
