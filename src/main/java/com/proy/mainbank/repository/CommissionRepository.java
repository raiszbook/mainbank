package com.proy.mainbank.repository;

import com.proy.mainbank.model.Client;
import com.proy.mainbank.model.Commission;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Repository
public interface CommissionRepository extends ReactiveCrudRepository<Commission, Long> {

    Flux<Commission> findByClientAndDateBetween(Client client, LocalDate startDate, LocalDate endDate);
}
