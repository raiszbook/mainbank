package com.proy.mainbank.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private CardType cardType;
    private String accountNumber;
    private double limit;
    private double balance; // Nuevo atributo para el saldo de la tarjeta de débito

    @ManyToOne
    private Client client;

    @ManyToOne
    private BankAccount bankAccount;

    public Card() {
        // Puedes inicializar los atributos si es necesario
    }

    public Card(CardType cardType, String accountNumber, double limit, double balance) {
        this.cardType = cardType;
        this.accountNumber = accountNumber;
        this.limit = limit;
        this.balance = balance;
    }


}
