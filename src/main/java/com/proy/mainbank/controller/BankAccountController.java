package com.proy.mainbank.controller;


import com.proy.mainbank.model.BankAccount;
import com.proy.mainbank.model.Client;
import com.proy.mainbank.model.Report;
import com.proy.mainbank.model.Transaction;
import com.proy.mainbank.service.BankAccountService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/bank-accounts")
public class BankAccountController {


    private final BankAccountService bankAccountService;

    @GetMapping("/client/{clientId}")
    public Flux<BankAccount> getBankAccountsByClient(@PathVariable Long clientId) {
        return bankAccountService.getBankAccountsByClient(new Client(clientId));
    }


    @GetMapping("/type/{accountType}")
    public Flux<BankAccount> getBankAccountsByAccountType(@PathVariable String accountType) {
        return bankAccountService.getBankAccountsByAccountType(accountType);
    }


    @GetMapping("/{accountNumber}")
    public Mono<BankAccount> getBankAccountByAccountNumber(@PathVariable String accountNumber) {
        return bankAccountService.getBankAccountByAccountNumber(accountNumber);
    }


    @GetMapping("/balance/{accountId}")
    public Mono<Report> checkBalance(BankAccount bankAccount) {
        BigDecimal balance = BigDecimal.valueOf(bankAccount.getBalance());
        Report balanceReport = new Report();
        balanceReport.setGenerationDate(LocalDateTime.now());
        balanceReport.setReportType("Balance Report");
        balanceReport.setClient(bankAccount.getPrimaryHolder());
        balanceReport.setReportData(List.of(balance));
        return Mono.just(balanceReport);
    }

    @GetMapping("/exists/{accountId}")
    public Mono<Boolean> existsBankAccountById(@PathVariable Long accountId) {
        return bankAccountService.existsBankAccountById(accountId);
    }

    @GetMapping("/deposit/{accountId}/{amount}")
    public Mono<Transaction> deposit(@PathVariable Long accountId, @PathVariable double amount) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(accountId);
        return bankAccountService.deposit(amount, bankAccount);
    }

    @GetMapping("/withdraw/{accountId}/{amount}")
    public Mono<Transaction> withdraw(@PathVariable Long accountId, @PathVariable double amount) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(accountId);
        return bankAccountService.withdraw(amount, bankAccount);
    }

    @GetMapping("/transfer/{sourceAccountId}/{targetAccountId}/{amount}")
    public Mono<Transaction> transfer(@PathVariable Long sourceAccountId, @PathVariable Long targetAccountId, @PathVariable double amount) {
        // Recuperar las cuentas bancarias de la base de datos
        Mono<BankAccount> sourceAccountMono = bankAccountService.getBankAccountById(sourceAccountId);
        Mono<BankAccount> targetAccountMono = bankAccountService.getBankAccountById(targetAccountId);

        // Realizar la transferencia cuando ambas cuentas estén disponibles
        return Mono.zip(sourceAccountMono, targetAccountMono)
                .flatMap(tuple -> {
                    BankAccount sourceAccount = tuple.getT1();
                    BankAccount targetAccount = tuple.getT2();

                    // Realizar la transferencia y retornar un Mono<Void> que refleje la finalización de la operación
                    return bankAccountService.transfer(BigDecimal.valueOf(amount), sourceAccount, targetAccount);
                });
    }

    @GetMapping("/report/daily-average-balances/{clientId}")
    public Mono<Report> generateDailyAverageBalancesReport(@PathVariable Long clientId) {
        Client client = new Client(clientId);
        return bankAccountService.generateDailyAverageBalancesReport(client);
    }

    @GetMapping("/report/commissions/{clientId}")
    public Mono<Report> generateCommissionsReport(@PathVariable Long clientId) {
        Client client = new Client(clientId);
        return Mono.just(bankAccountService.generateCommissionsReport(client));
    }

}
