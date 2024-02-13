package com.proy.mainbank.repository;

import com.proy.mainbank.model.Client;
import com.proy.mainbank.model.ClientType;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ClientRepository extends ReactiveCrudRepository<Client, Long> {

    Flux<Client> findByClientType(ClientType clientType);

    Flux<Client> findByName(String name);

    Mono<Client> findByContactInfo(String contactInfo);

    // Método para verificar si un cliente tiene productos de crédito asociados
    Mono<Boolean> existsClientByIdAndCreditProductIsNotNull(Long id);
}
