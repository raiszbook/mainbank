package com.proy.mainbank.repository;


import com.proy.mainbank.model.Wallet;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public interface WalletRepository extends ReactiveCrudRepository<Wallet,Long> {

    Mono<Wallet> findByUserId(Long userId);


}
