package com.nttdata.bc46account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Ntt Data - Top Employer 2023.
 * Todos los derechos Reservados.
 */
@EnableEurekaClient
@SpringBootApplication
public class Bc46AccountApplication {

  public static void main(String[] args) {
    SpringApplication.run(Bc46AccountApplication.class, args);
  }

}
