package com.epam.taxi.facade.configuration;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
@Data
@Validated
public class KafkaProducerProperties {

    @NotEmpty
    private List<String> bootstrapServers;
    @NotBlank
    private String topic;
    private Duration deliveryTimeout;
}
