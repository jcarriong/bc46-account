package com.nttdata.bc46account.model;

import java.util.List;
import lombok.*;

/**
 * Ntt Data - Top Employer 2023.
 * Todos los derechos Reservados.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Persona {

  private String nombre; //nombre del titular o firmante autorizado.*
  private String dni; //numero de documento
  private String rol; //rol de la persona

}
