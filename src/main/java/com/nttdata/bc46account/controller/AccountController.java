package com.nttdata.bc46account.controller;

import com.nttdata.bc46account.model.Account;
import com.nttdata.bc46account.model.Movement;
import com.nttdata.bc46account.service.AccountService;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Ntt Data - Top Employer 2023.
 * Todos los derechos Reservados.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class AccountController {

  @Autowired
  private AccountService bankAccountService;

  /**
   * Consultar todas las cuentas bancarias existentes.
   **/
  @GetMapping("/findAll")
  public Flux<Account> findAll() {
    log.info("All bank accounts were consulted");
    return bankAccountService.findAll()
        .doOnNext(bankAccount -> bankAccount.toString());
  }

  /**
   * Consultar cuentas bancarias por idCustomer.
   **/
  @GetMapping("/findAccountsByCustomer/{idCustomer}")
  public Flux<Account> findByIdCustomer(@PathVariable("idCustomer") String idCustomer) {
    log.info("Bank account was consulted by idCustomer");
    return bankAccountService.findByIdCustomer(idCustomer);
  }

  /**
   * Consultar cuenta bancaria por idAccount.
   **/
  @GetMapping("/findById/{id}")
  public Mono<ResponseEntity<Account>> findById(@PathVariable("id") String id) {
    log.info("Bank account consulted by id " + id);
    return bankAccountService.findById(id)
        .map(ResponseEntity::ok)
        .switchIfEmpty(Mono.error(() -> new RuntimeException("No se encontró la cuenta bancaria")));

  }

  /**
   * Crear una cuenta bancaria de un producto relacionado.
   **/
  @PostMapping("/saveAccount")
  public Mono<ResponseEntity<Account>> save(@RequestBody Account bankAccount) {
    log.info("A bank account was created");
    bankAccount.setCreationDatetime(LocalDateTime.now());
    return bankAccountService.save(bankAccount)
        .map(bc -> new ResponseEntity<>(bc, HttpStatus.CREATED))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Editar datos de una cuenta bancaria(se restringe la edición de llaves compuestas).
   **/
  @PutMapping("/updateAccountById/{idAccount}")
  public Mono<ResponseEntity<Account>> update(@RequestBody Account bankAccount,
                                              @PathVariable("idAccount") String idAccount) {
    log.info("A bank account was changed");
    return bankAccountService.updateAccount(bankAccount, idAccount)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.badRequest().build());
  }

  /**
   * Eliminar una cuenta bancaria del registro.
   **/
  @DeleteMapping("/deleteCustomerById/{idAccount}")
  public Mono<ResponseEntity<Void>> deleteAccountById(@PathVariable(name = "idAccount")
                                                      String idAccount) {
    log.info("A bank account was deleted");
    return bankAccountService.deleteAccountById(idAccount)
        .map(bankCustomer -> ResponseEntity.ok().<Void>build())
        .defaultIfEmpty(ResponseEntity.notFound().build());

  }

  /**
   * Agregar un movimiento bancario -falta mejorar lógica
   **/
  @PostMapping("/addOperationToAccount/{idAccount}")
  public Mono<ResponseEntity<Movement>> save(@PathVariable String idAccount,
                                             @RequestBody Movement movement) {
    log.info("A bank movement was inserted");
    movement.setCreationDatetime(LocalDateTime.now());
    return bankAccountService.addOperationToAccount(idAccount, movement)
        .map(bc -> new ResponseEntity<>(bc, HttpStatus.CREATED));
  }
}
