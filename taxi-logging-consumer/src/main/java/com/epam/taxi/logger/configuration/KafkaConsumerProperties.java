package com.epam.taxi.logger.configuration;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class KafkaConsumerProperties {

  @NotEmpty
  private String topic;
  @NotEmpty
  private String consumerGroupId;
  @NotEmpty
  private List<String> bootstrapServers;
}
