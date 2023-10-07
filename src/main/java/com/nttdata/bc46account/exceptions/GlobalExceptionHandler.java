package com.nttdata.bc46account.exceptions;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AccountNotFoundException.class)
  public ResponseEntity<Object> handleAccountNotFoundException(AccountNotFoundException ex) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("code", "ACCOUNT_NOT_FOUND");
    body.put("message", ex.getMessage());

    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(DuplicateAccountException.class)
  public ResponseEntity<Object> handleDuplicateAccountException(DuplicateAccountException ex) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("code", "PERSONAL_ACCOUNT_DUPLICATE");
    body.put("message", ex.getMessage());

    return new ResponseEntity<>(body, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(InvalidAccountTypeException.class)
  public ResponseEntity<Object> handleInvalidAccountTypeException(InvalidAccountTypeException ex) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("code", "BUSINESS_ACCOUNT_INVALID");
    body.put("message", ex.getMessage());

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

}