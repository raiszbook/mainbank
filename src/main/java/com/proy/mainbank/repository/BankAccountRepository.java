package com.proy.mainbank.repository;

import com.proy.mainbank.model.AccountType;
import com.proy.mainbank.model.BankAccount;
import com.proy.mainbank.model.Client;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BankAccountRepository extends ReactiveCrudRepository<BankAccount,Long> {

    Flux<BankAccount> findByClient(Client client);
    Flux<BankAccount> findByAccountType(AccountType accountType);
    Mono<BankAccount> findByAccountNumber(String accountNumber);

    Mono<BankAccount> save(BankAccount bankAccount);

}
