package com.proy.mainbank.service;


import com.proy.mainbank.model.*;
import com.proy.mainbank.repository.CommissionRepository;
import com.proy.mainbank.repository.CreditProductRepository;
import com.proy.mainbank.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class CreditProductService {


    private final CreditProductRepository creditProductRepository;
    private final CommissionRepository commissionRepository;
    private final TransactionRepository transactionRepository;

    // Obtener productos de crédito por cliente.
    public Flux<CreditProduct> getCreditProductsByClient(Client client) {
        return creditProductRepository.findByClient(client);
    }

    // Verificar si un cliente tiene productos de crédito asociados.
    public Mono<Boolean> hasCreditProducts(Client client) {
        return creditProductRepository.existsByClient(client);
    }

    public Mono<Void> makePayment(CreditProduct creditProduct, double amount) {
        // Verifica si el monto es válido (mayor que cero)
        if (amount <= 0) {
            return Mono.error(new IllegalArgumentException("El monto del pago debe ser mayor que cero"));
        }

        // Verifica si el saldo disponible en el producto de crédito es suficiente para el pago
        if (creditProduct.getBalance() < amount) {
            return Mono.error(new IllegalArgumentException("Saldo insuficiente en el producto de crédito para realizar el pago"));
        }

        // Crea una nueva transacción para el pago
        Transaction paymentTransaction = new Transaction();
        paymentTransaction.setTransactionType(TransactionType.PAY);
        paymentTransaction.setAmount(amount);
        paymentTransaction.setDate(LocalDateTime.now());
        paymentTransaction.setCreditProduct(creditProduct);

        // Actualiza el balance del producto de crédito
        double newBalance = creditProduct.getBalance() - amount;
        creditProduct.setBalance(newBalance);

        // Guarda la transacción y actualiza el producto de crédito
        transactionRepository.save(paymentTransaction);
        return Mono.empty(); // Devuelve un Mono<Void> vacío
    }

    // Generar informe de saldos diarios promedio.
    public Mono<Report> generateDailyAverageBalancesReport(Client client) {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        return creditProductRepository.findByClient(client)
                .collectList()
                .flatMap(creditProducts -> {
                    double totalBalance = creditProducts.stream().mapToDouble(CreditProduct::getBalance).sum();
                    int days = creditProducts.size();
                    double dailyAverageBalance = totalBalance / days;

                    Report dailyAverageBalancesReport = new Report(LocalDateTime.now(), client, "Daily Average Balances", List.of(dailyAverageBalance));
                    return Mono.just(dailyAverageBalancesReport);
                });
    }

    // Generar informe de comisiones.
    public Mono<Report> generateCommissionsReport(Client client) {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        return commissionRepository.findByClientAndDateBetween(client, firstDayOfMonth, lastDayOfMonth)
                .collectList()
                .flatMap(commissions -> {
                    List<Object> commissionsAsObjects = new ArrayList<>(commissions);
                    Report commissionsReport = new Report(LocalDateTime.now(), client, "Commissions Report", commissionsAsObjects);
                    return Mono.just(commissionsReport);
                });
    }


}
