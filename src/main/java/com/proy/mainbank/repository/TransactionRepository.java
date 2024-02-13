package com.proy.mainbank.repository;

import com.proy.mainbank.model.BankAccount;
import com.proy.mainbank.model.Card;
import com.proy.mainbank.model.Client;
import com.proy.mainbank.model.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public interface TransactionRepository extends ReactiveCrudRepository<Transaction,Long> {

    Flux<Transaction> findByBankAccount(BankAccount bankAccount);


    Flux<Transaction> findByClientAndDateBetween(Client client, LocalDate startDate, LocalDate endDate);

    Flux<Transaction> findByBankAccountOrderByDateDesc(BankAccount bankAccount, Pageable pageable);

    Flux<Transaction> findTop10ByCardOrderByDateDesc(Card card);

    Mono<Transaction> save(Transaction transaction);

    Flux<Transaction> findByCardOrderByDateDesc(Card card, Pageable pageable);


}
