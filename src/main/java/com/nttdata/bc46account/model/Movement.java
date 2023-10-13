package com.nttdata.bc46account.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "movement")
public class Movement implements Serializable {
  @Id
  private String idMovement;
  private String operation; //Operaciones: Transferir dinero, Pagar servicios
  private String movementType; //Depósito, Retiro, Pago, Tran.Ctas.Prop, Tran.Ctas.Terc
  private String sourceAccount;
  private String targetAccount;
  private String moneda;
  private Float monto;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss",
      timezone = "America/Lima")
  @CreatedDate
  private LocalDateTime creationDatetime;

}
