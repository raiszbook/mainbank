package com.proy.mainbank.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String identificationNumber;
    private IdentificationType identificationType;
    private String phoneNumber;
    private String imei;
    private String email;
    private String debitCardNumber; // Número de tarjeta de débito asociada
    private BigDecimal balance;

    @ManyToOne
    private Client client;

    @ManyToOne
    private BankAccount bankAccount;

    @ManyToOne
    private DebitCard debitCard;

    public Wallet() {
        // Puedes inicializar los atributos si es necesario
    }


}