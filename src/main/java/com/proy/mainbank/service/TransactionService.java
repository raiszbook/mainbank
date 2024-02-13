package com.proy.mainbank.service;


import com.proy.mainbank.model.BankAccount;
import com.proy.mainbank.model.Card;
import com.proy.mainbank.model.Transaction;
import com.proy.mainbank.model.TransactionType;
import com.proy.mainbank.repository.BankAccountRepository;
import com.proy.mainbank.repository.CardRepository;
import com.proy.mainbank.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@AllArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final CardRepository cardRepository;

    public Flux<Transaction> getTransactionsByBankAccount(BankAccount bankAccount) {
        return transactionRepository.findByBankAccount(bankAccount);
    }

    public Mono<Void> performTransaction(TransactionType transactionType, double amount, BankAccount sourceAccount, BankAccount targetAccount) {
        switch (transactionType) {
            case DEPOSIT:
                return deposit(amount, sourceAccount);
            case WITHDRAWAL:
                return withdraw(amount, sourceAccount);
            case TRANSFER:
                return transfer(amount, sourceAccount, targetAccount);
            default:
                return Mono.error(new IllegalArgumentException("Invalid transaction type"));
        }
    }


    private Mono<Void> deposit(double amount, BankAccount bankAccount) {
        BigDecimal currentBalance = BigDecimal.valueOf(bankAccount.getBalance());
        BigDecimal newBalance = currentBalance.add(BigDecimal.valueOf(amount));

        bankAccount.setBalance(newBalance.doubleValue());

        return bankAccountRepository.save(bankAccount)
                .flatMap(savedAccount -> {
                    Transaction transaction = new Transaction(TransactionType.DEPOSIT, amount, LocalDateTime.now(), savedAccount);
                    transactionRepository.save(transaction);
                    return Mono.empty(); // Devolver un Mono vacío como resultado final
                });
    }

    private Mono<Void> withdraw(double amount, BankAccount bankAccount) {
        BigDecimal currentBalance = BigDecimal.valueOf(bankAccount.getBalance());
        BigDecimal withdrawAmount = BigDecimal.valueOf(amount);

        if (currentBalance.compareTo(withdrawAmount) >= 0) {
            BigDecimal newBalance = currentBalance.subtract(withdrawAmount);
            bankAccount.setBalance(newBalance.doubleValue());
            return bankAccountRepository.save(bankAccount).then(Mono.fromRunnable(() -> {
                Transaction transaction = new Transaction(TransactionType.WITHDRAWAL, amount, LocalDateTime.now(), bankAccount);
                transactionRepository.save(transaction);
            }));
        } else {
            return Mono.error(new IllegalArgumentException("Insufficient funds"));
        }
    }

    private Mono<Void> transfer(double amount, BankAccount sourceAccount, BankAccount targetAccount) {
        BigDecimal transferAmount = BigDecimal.valueOf(amount);
        BigDecimal sourceBalance = BigDecimal.valueOf(sourceAccount.getBalance());

        if (sourceBalance.compareTo(transferAmount) >= 0) {
            BigDecimal newSourceBalance = sourceBalance.subtract(transferAmount);
            BigDecimal targetBalance = BigDecimal.valueOf(targetAccount.getBalance()).add(transferAmount);

            sourceAccount.setBalance(newSourceBalance.doubleValue());
            targetAccount.setBalance(targetBalance.doubleValue());

            // Guardar ambas cuentas y luego guardar las transacciones en paralelo
            return bankAccountRepository.save(sourceAccount)
                    .then(bankAccountRepository.save(targetAccount))
                    .thenMany(Flux.fromIterable(Arrays.asList(
                            new Transaction(TransactionType.WITHDRAWAL, amount, LocalDateTime.now(), sourceAccount),
                            new Transaction(TransactionType.DEPOSIT, amount, LocalDateTime.now(), targetAccount)
                    )))
                    .flatMap(transactionRepository::save)
                    .then();
        } else {
            return Mono.error(new IllegalArgumentException("Insufficient funds for transfer"));
        }
    }


    public Flux<Transaction> getLatestTransactionsByBankAccount(BankAccount bankAccount) {
        return transactionRepository.findByBankAccountOrderByDateDesc(bankAccount, PageRequest.of(0, 10));
    }

    public Mono<Void> makeDebitCardPayment(Card debitCard, double amount) {
        // Verifica si el saldo de la tarjeta es suficiente para realizar el pago
        if (debitCard.getBalance() >= amount) {
            // Actualiza el saldo de la tarjeta
            debitCard.setBalance(debitCard.getBalance() - amount);
            // Guarda la tarjeta actualizada
            return cardRepository.save(debitCard)
                    .flatMap(savedCard -> {
                        // Busca la tarjeta por su número para obtener el número de cuenta asociado
                        return cardRepository.findByCardNumber(savedCard.getAccountNumber())
                                .flatMap(foundCard -> {
                                    // Obtiene el número de cuenta asociado a la tarjeta
                                    String accountNumber = foundCard.getAccountNumber();
                                    // Busca la cuenta bancaria por su número
                                    return bankAccountRepository.findByAccountNumber(accountNumber)
                                            .flatMap(bankAccount -> {
                                                // Crea una nueva transacción
                                                Transaction transaction = new Transaction(TransactionType.PAY, amount, LocalDateTime.now(), bankAccount);
                                                // Guarda la transacción
                                                return transactionRepository.save(transaction).then();
                                            });
                                });
                    });
        } else {
            // Si el saldo de la tarjeta no es suficiente, devuelve un error
            return Mono.error(new IllegalArgumentException("Insufficient funds in the debit card"));
        }
    }

    public Flux<Transaction> getLatestTransactionsByDebitCard(Card debitCard) {
        return transactionRepository.findByCardOrderByDateDesc(debitCard, PageRequest.of(0, 10));
    }
}

