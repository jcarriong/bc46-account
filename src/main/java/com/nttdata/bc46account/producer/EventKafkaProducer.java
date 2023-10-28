package com.nttdata.bc46account.producer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nttdata.bc46account.model.Movement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventKafkaProducer {

  private final KafkaTemplate<Object, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  public EventKafkaProducer(@Qualifier("kafkaTemplate") KafkaTemplate<Object, String> kafkaTemplate, ObjectMapper objectMapper) {
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper;
  }

  public void enviarMovimiento(Movement movement) {
    /**Publica el evento en el topic de kafka */
    try {
      String movementJson = objectMapper.writeValueAsString(movement);

      log.info("producing account movement message {}", movement);
      this.kafkaTemplate.send("topic-movimientos-cuentas", movementJson);
    } catch (JsonProcessingException e) {
      log.error("Error al serializar el movimiento a JSON", e);
    }
  }
}
