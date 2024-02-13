package com.proy.mainbank.controller;


import com.proy.mainbank.model.BankAccount;
import com.proy.mainbank.model.Card;
import com.proy.mainbank.model.Transaction;
import com.proy.mainbank.service.BankAccountService;
import com.proy.mainbank.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
public class TransactionController {

    private final TransactionService transactionService;

    private final BankAccountService bankAccountService;

    @GetMapping("/transactions/bank-account")
    public Flux<Transaction> getTransactionsByBankAccount(@RequestParam("accountNumber") String accountNumber) {
        return bankAccountService.getBankAccountByAccountNumber(accountNumber)
                .flatMapMany(bankAccount -> transactionService.getTransactionsByBankAccount(bankAccount));
    }

    @PostMapping("/transactions/debit-card-payment")
    public Mono<Void> makeDebitCardPayment(@RequestBody Card debitCard,@RequestBody double amount) {
        return transactionService.makeDebitCardPayment(debitCard , amount);
    }

    @GetMapping("/transactions/latest/debit-card")
    public Flux<Transaction> getLatestTransactionsByDebitCard(@RequestBody Card debitCard) {
        return transactionService.getLatestTransactionsByDebitCard(debitCard);
    }



    @GetMapping("/transactions/latest/bank-account")
    public Flux<Transaction> getLatestTransactionsByBankAccount(@RequestBody BankAccount bankAccount) {
        return transactionService.getLatestTransactionsByBankAccount(bankAccount);
    }

}
