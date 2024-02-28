package com.epam.taxi.tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.epam.taxi")
public class TaxiTrackerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaxiTrackerApplication.class, args);
    }
}
