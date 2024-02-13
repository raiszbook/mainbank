package com.proy.mainbank.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class Transaction {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private double amount;
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "bank_account_id")
    private BankAccount bankAccount;

    @ManyToOne
    @JoinColumn(name = "credit_product_id")
    private CreditProduct creditProduct;

    // Constructor vacío requerido por JPA
    public Transaction() {
    }

    public Transaction(TransactionType transactionType, double amount, LocalDateTime date,  BankAccount bankAccount) {
        this.transactionType = transactionType;
        this.date=date;
        this.amount = amount;
        this.bankAccount = bankAccount;
    }

}
