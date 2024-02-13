package com.proy.mainbank.service;


import com.proy.mainbank.model.*;
import com.proy.mainbank.repository.BankAccountRepository;
import com.proy.mainbank.repository.CommissionRepository;
import com.proy.mainbank.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final CommissionRepository commissionRepository;


    //Obtener cuentas bancarias por cliente.
    public Flux<BankAccount> getBankAccountsByClient(Client client) {
        return bankAccountRepository.findByClient(client);
    }


    //Obtener cuenta bancaria por número de cuenta.
    public Mono<BankAccount> getBankAccountByAccountNumber(String accountNumber) {
        return bankAccountRepository.findByAccountNumber(accountNumber);
    }

    //Obtener cuentas bancarias por tipo de cuenta.
    public Flux<BankAccount> getBankAccountsByAccountType(String accountType) {
        AccountType type = AccountType.valueOf(accountType.toUpperCase());
        return bankAccountRepository.findByAccountType(type);
    }




//Verificar si una cuenta bancaria existe por su ID.
public Mono<Boolean> existsBankAccountById(Long id) {
    return bankAccountRepository.existsById(id);
}

    public Mono<Transaction> deposit(double amount, BankAccount bankAccount) {
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        return bankAccountRepository.save(bankAccount)
                .then(transactionRepository.save(new Transaction(TransactionType.DEPOSIT, amount, LocalDateTime.now(), bankAccount)));
    }



    //Realizar depósitos y retiros en cuentas bancarias.
    public Mono<Transaction> withdraw(double amount, BankAccount bankAccount) {
        return Mono.defer(() -> {
            BigDecimal balance = BigDecimal.valueOf(bankAccount.getBalance());
            BigDecimal withdrawAmount = BigDecimal.valueOf(amount);

            if (balance.compareTo(withdrawAmount) >= 0) {
                BigDecimal newBalance = balance.subtract(withdrawAmount);
                bankAccount.setBalance(newBalance.doubleValue());
                return bankAccountRepository.save(bankAccount)
                        .then(transactionRepository.save(new Transaction(TransactionType.WITHDRAWAL, amount, LocalDateTime.now(), bankAccount)));
            } else {
                return Mono.error(new IllegalArgumentException("Insufficient balance"));
            }
        });
    }

    //Realizar transferencias entre cuentas del mismo cliente y a terceros.
    public Mono<Transaction> transfer(BigDecimal amount, BankAccount sourceAccount, BankAccount targetAccount) {
        return Mono.defer(() -> {
            BigDecimal sourceBalance = BigDecimal.valueOf(sourceAccount.getBalance());
            BigDecimal targetBalance = BigDecimal.valueOf(targetAccount.getBalance());

            if (sourceBalance.compareTo(amount) >= 0) {
                BigDecimal amountNegated = amount.negate();
                BigDecimal newSourceBalance = sourceBalance.subtract(amount);
                BigDecimal newTargetBalance = targetBalance.add(amount);

                sourceAccount.setBalance(newSourceBalance.doubleValue());
                targetAccount.setBalance(newTargetBalance.doubleValue());

                Transaction withdrawalTransaction = new Transaction(TransactionType.WITHDRAWAL, amountNegated.doubleValue(), LocalDateTime.now(), sourceAccount);
                Transaction depositTransaction = new Transaction(TransactionType.DEPOSIT, amount.doubleValue(), LocalDateTime.now(), targetAccount);


                return bankAccountRepository.saveAll(List.of(sourceAccount, targetAccount))
                        .then(transactionRepository.save(withdrawalTransaction))
                        .then(transactionRepository.save(depositTransaction))
                        .thenReturn(depositTransaction);
            } else {
                return Mono.error(new IllegalArgumentException("Insufficient balance"));
            }
        });
    }

    //Consultar el saldo de una cuenta bancaria.
    public BigDecimal checkBalance(BankAccount bankAccount) {
        return BigDecimal.valueOf(bankAccount.getBalance());
    }


    // Generar un resumen de los saldos promedio diarios del mes en curso
    public Mono<Report> generateDailyAverageBalancesReport(Client client) {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        Flux<Transaction> transactionsFlux = transactionRepository.findByClientAndDateBetween(client, firstDayOfMonth, lastDayOfMonth);

        return transactionsFlux.collectList()
                .flatMap(transactions -> {
                    double totalBalance = transactions.stream()
                            .mapToDouble(transaction -> transaction.getAmount()) // Cambia a la cantidad de la transacción según tu modelo
                            .sum();
                    long days = ChronoUnit.DAYS.between(firstDayOfMonth, lastDayOfMonth) + 1; // Calcula el número de días en el mes

                    double dailyAverageBalance = totalBalance / days;

                    // Crear el informe
                    Report dailyAverageBalancesReport = new Report(
                            LocalDateTime.now(),
                            client,
                            "Daily Average Balances",
                            List.of(dailyAverageBalance)
                    );

                    dailyAverageBalancesReport.setGenerationDate(LocalDateTime.now());
                    dailyAverageBalancesReport.setClient(client);
                    dailyAverageBalancesReport.setReportType("Daily Average Balances");
                    dailyAverageBalancesReport.setReportData(List.of(dailyAverageBalance));

                    return Mono.just(dailyAverageBalancesReport);
                });
    }



    //Generar un reporte de todas las comisiones cobradas por producto en el mes actual.
    public Report generateCommissionsReport(Client client) {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        Flux<Commission> commissionsFlux = commissionRepository.findByClientAndDateBetween(client, firstDayOfMonth, lastDayOfMonth);

        // Convertir el Flux a una lista
        List<Commission> commissions = commissionsFlux.collectList().block(); // Bloquear para obtener la lista de forma síncrona

        // Crear el informe
        Report commissionsReport = new Report(LocalDateTime.now(), client, "Commissions Report", new ArrayList<>(commissions));

        return commissionsReport;
    }


    // Obtener una cuenta bancaria por su ID
    public Mono<BankAccount> getBankAccountById(Long id) {
        return bankAccountRepository.findById(id).switchIfEmpty(Mono.empty());
    }

}
