package com.nttdata.bc46account.service;

import com.nttdata.bc46account.model.Account;
import com.nttdata.bc46account.repository.AccountRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AccountImpl implements AccountService {
  @Autowired
  AccountRepository accountRepository;

  @Override
  public Flux<Account> findAll() {
    return accountRepository.findAll();
  }

  @Override
  public Flux<Account> findByIdCustomer(String idCustomer) {
    return accountRepository.findByIdCustomer(idCustomer);
  }

  @Override
  public Mono<Account> findById(String id) {
    return accountRepository.findById(id);
  }

  @Override
  public Mono<Account> save(Account bankAccount) {
    return accountRepository.save(bankAccount);
  }

  @Override
  public Mono<Account> updateAccount(Account account, String idAccount) {

    return accountRepository.findById(idAccount)
        .flatMap(currentBankAccount -> {
          currentBankAccount.setAvailableBalance(account.getAvailableBalance());
          currentBankAccount.setHolderAccount(account.getHolderAccount());
          currentBankAccount.setAuthorizedSigner(account.getAuthorizedSigner());
          currentBankAccount.setUpdateDatetime(LocalDateTime.now());
          return accountRepository.save(currentBankAccount);
        });

  }

  @Override
  public Mono<Account> deleteAccountById(String idAccount) {
    return accountRepository.findById(idAccount)
        .flatMap(existingAccount -> accountRepository.delete(existingAccount)
            .then(Mono.just(existingAccount)));
  }
}
