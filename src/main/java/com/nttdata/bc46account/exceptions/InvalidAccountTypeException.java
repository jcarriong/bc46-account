package com.nttdata.bc46account.exceptions;

public class InvalidAccountTypeException extends RuntimeException {
  public InvalidAccountTypeException(String message) {
    super(message);
  }
}
