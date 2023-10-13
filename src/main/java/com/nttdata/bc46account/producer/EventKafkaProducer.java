package com.nttdata.bc46account.producer;

import com.nttdata.bc46account.model.Movement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventKafkaProducer {

  private final KafkaTemplate<String, Movement> kafkaTemplate;

  public EventKafkaProducer(@Qualifier("kafkaTemplate") KafkaTemplate<String, Movement> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void enviarMovimiento(Movement movement) {
    /**Publica el evento en el topic de kafka */
    log.info("producing account movement message {}", movement);
    this.kafkaTemplate.send("topic-movimientos-cuentas", movement);
  }
}
