package com.proy.mainbank.repository;

import com.proy.mainbank.model.Card;
import com.proy.mainbank.model.Client;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface CardRepository extends ReactiveCrudRepository<Card, Long> {

    Flux<Card> findByClient(Client client);

    Mono<Card> findByNumber(String number);

    Mono<Card> findByCardNumber(String cardNumber);

}
