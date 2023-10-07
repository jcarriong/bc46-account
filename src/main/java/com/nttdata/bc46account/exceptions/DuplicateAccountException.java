package com.nttdata.bc46account.exceptions;

public class DuplicateAccountException extends RuntimeException {
  public DuplicateAccountException(String message) {
    super(message);
  }
}
