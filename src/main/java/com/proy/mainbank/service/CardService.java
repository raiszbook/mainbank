package com.proy.mainbank.service;

import com.proy.mainbank.model.Card;
import com.proy.mainbank.model.Client;
import com.proy.mainbank.model.Transaction;
import com.proy.mainbank.repository.CardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@AllArgsConstructor
@Service
public class CardService {

    private final CardRepository cardRepository;
    private final TransactionService transactionService;


    public Flux<Card> getCardsByClient(Long clientId) {
        return cardRepository.findByClient(new Client(clientId));
    }


    public Mono<Card> getCardByNumber(String number) {
        return cardRepository.findByNumber(number);
    }


    public Mono<Void> makeDebitCardPayment(String cardNumber, double amount) {
        return cardRepository.findByNumber(cardNumber)
                .flatMap(card -> {
                    if (card != null) {
                        return transactionService.makeDebitCardPayment(card, amount);
                    } else {
                        return Mono.error(new IllegalArgumentException("No se encontró la tarjeta con el número: " + cardNumber));
                    }
                });
    }


    public Flux<Transaction> getLatestTransactionsByDebitCard(Card debitCard) {
        return transactionService.getLatestTransactionsByDebitCard(debitCard);
    }
}