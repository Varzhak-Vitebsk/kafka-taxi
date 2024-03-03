package com.epam.taxi.tracker.configuration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

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
