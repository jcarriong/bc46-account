package com.nttdata.bc46account.service;

import com.nttdata.bc46account.model.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Ntt Data - Top Employer 2023.
 * Todos los derechos Reservados.
 */
public interface AccountService {
  Flux<Account> findAll();

  Flux<Account> findByIdCustomer(String idCustomer);

  Mono<Account> findById(String id);

  Mono<Account> save(Account account);

  Mono<Account> updateAccount(Account account, String idAccount);

  Mono<Account> deleteAccountById(String idAccount);
}
