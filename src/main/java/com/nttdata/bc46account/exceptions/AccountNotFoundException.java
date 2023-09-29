package com.nttdata.bc46account.exceptions;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Ntt Data - Top Employer 2023.
 * Todos los derechos Reservados.
 */
@Data
@Getter
@Setter
public class AccountNotFoundException extends RuntimeException {
  private final String idAccount;
  private static final String msg = "El idAccount: ";

  public AccountNotFoundException(String idAccount) {
    super(msg.concat(idAccount).concat(" es inválido."));
    this.idAccount = idAccount;
  }

}
