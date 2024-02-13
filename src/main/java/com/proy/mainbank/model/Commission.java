package com.proy.mainbank.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
public class Commission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Client client;

    private BigDecimal amount;

    private LocalDate date;

    public Commission() {
        // Puedes inicializar los atributos si es necesario
    }
}
