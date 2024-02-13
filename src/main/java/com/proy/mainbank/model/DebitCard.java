package com.proy.mainbank.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@Entity
public class DebitCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String number;
    private double limit;

    @ManyToOne
    private Client client;

    @ManyToOne
    private BankAccount bankAccount;

    public DebitCard() {
        // Puedes inicializar los atributos si es necesario
    }

    public DebitCard(Long id, String number, double limit) {
        this.id = id;
        this.number = number;
        this.limit = limit;
    }

}