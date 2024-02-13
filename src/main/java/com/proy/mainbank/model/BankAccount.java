package com.proy.mainbank.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private AccountType accountType;
    private double balance;
    private int limitMonthlyMovements;

    @ManyToMany
    private List<Client> holders;

    @ManyToMany
    private List<Client> authorizedSigners;

    @OneToMany(mappedBy = "bankAccount")
    private List<Transaction> transactions;

    public BankAccount() {
        // Puedes inicializar los atributos si es necesario
    }

    // Constructor
    public BankAccount(Long id, AccountType accountType, double balance, int limitMonthlyMovements) {
        this.id = id;
        this.accountType = accountType;
        this.balance = balance;
        this.limitMonthlyMovements = limitMonthlyMovements;
        this.transactions = new ArrayList<>(); // Inicializa la lista de transacciones
    }

    public Client getPrimaryHolder() {
        if (holders != null && !holders.isEmpty()) {
            return holders.get(0); // Devuelve el primer titular como el titular principal
        } else {
            return null; // Manejar el caso cuando no hay titulares
        }
    }

}
