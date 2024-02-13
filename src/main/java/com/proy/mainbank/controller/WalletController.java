package com.proy.mainbank.controller;

import com.proy.mainbank.model.Wallet;
import com.proy.mainbank.service.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/yanki")
@AllArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/register")
    public Mono<Object> registerYankiUser(@RequestBody Wallet wallet) {
        return walletService.registerYankiUser(wallet);
    }

    @PostMapping("/associate-debit-card/{yankiUserId}")
    public Mono<Void> associateDebitCardToYankiUser(@PathVariable Long yankiUserId, @RequestParam String debitCardNumber) {
        return walletService.associateDebitCardToYankiUser(yankiUserId, debitCardNumber);
    }

    @PostMapping("/transfer/{sourceYankiUserId}/{targetYankiUserId}/{amount}")
    public Mono<Void> transferYankiBalance(@PathVariable Long sourceYankiUserId, @PathVariable Long targetYankiUserId, @PathVariable double amount) {
        return walletService.transferYankiBalance(sourceYankiUserId, targetYankiUserId, amount);
    }

    @GetMapping("/balance/{yankiUserId}")
    public Mono<Double> getYankiBalance(@PathVariable Long yankiUserId) {
        return walletService.getYankiBalance(yankiUserId);
    }
}