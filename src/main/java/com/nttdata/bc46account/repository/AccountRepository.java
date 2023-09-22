package com.nttdata.bc46account.repository;

import com.nttdata.bc46account.model.Account;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AccountRepository extends ReactiveMongoRepository<Account, String> {
  Flux<Account> findByIdCustomer(String idCustomer);
}