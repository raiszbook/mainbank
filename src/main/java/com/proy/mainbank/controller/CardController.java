package com.proy.mainbank.controller;

import com.proy.mainbank.model.Card;
import com.proy.mainbank.model.Transaction;
import com.proy.mainbank.service.CardService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@RestController
@RequestMapping("/cards")
@AllArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping("/client/{clientId}")
    public Flux<Card> getCardsByClient(@PathVariable Long clientId) {
        return cardService.getCardsByClient(clientId);
    }

    @GetMapping("/{cardNumber}")
    public Mono<Card> getCardByNumber(@PathVariable String cardNumber) {
        return cardService.getCardByNumber(cardNumber);
    }

    @PostMapping("/{cardNumber}/payments")
    public Mono<Void> makeDebitCardPayment(@PathVariable String cardNumber, @RequestParam double amount) {
        return cardService.makeDebitCardPayment(cardNumber, amount);
    }

    @GetMapping("/{cardNumber}/transactions")
    public Flux<Transaction> getLatestTransactionsByDebitCard(@PathVariable String cardNumber) {
        return cardService.getCardByNumber(cardNumber)
                .flatMapMany(card -> cardService.getLatestTransactionsByDebitCard(card));
    }
}
