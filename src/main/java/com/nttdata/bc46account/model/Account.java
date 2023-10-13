package com.nttdata.bc46account.model;

import java.util.List;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Ntt Data - Top Employer 2023.
 * Todos los derechos Reservados.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@NonNull
@Builder
@Document(collection = "account")
public class Account extends BaseAuditDto {

  @Id
  private String idAccount;
  private String accountType; //tipo de cuenta: personal | empresarial
  @NonNull
  private String idProduct;
  @NonNull
  private String idCustomer;
  @NonNull
  private String accountNumber; //numero de cuenta (14 digits)
  @NonNull
  private String cci; //numero de cuenta interbancaria (20 digits)
  private Float availableBalance; //saldo disponible
  private List<Persona> holderAccount; //titular de la cuenta 1.*
  private List<Persona> authorizedSigner; //firmante autorizado 0.4
  private List<Movement> bankMovements; //lista de movimientos bancarios

}
