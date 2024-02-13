package com.proy.mainbank.repository;


import com.proy.mainbank.model.Client;
import com.proy.mainbank.model.CreditProduct;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface CreditProductRepository  extends ReactiveCrudRepository<CreditProduct, Long> {


    Flux<CreditProduct> findByClient(Client client);

    Mono<Boolean> existsByClient(Client client);


}
