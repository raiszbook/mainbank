package com.proy.mainbank.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class CreditProduct {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private CreditType creditType;
    private double balance;
    private double creditLimit;

    @ManyToOne
    private Client client;

    @OneToMany(mappedBy = "creditProduct")
    private List<Transaction> transactions;


    public CreditProduct() {
        // Puedes inicializar los atributos si es necesario
    }

    // Constructor
    public CreditProduct(Long id, CreditType creditType, double balance, double creditLimit) {
        this.id = id;
        this.creditType = creditType;
        this.balance = balance;
        this.creditLimit = creditLimit;
    }

}
