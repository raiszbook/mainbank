package com.proy.mainbank.controller;

import com.proy.mainbank.model.Client;
import com.proy.mainbank.model.CreditProduct;
import com.proy.mainbank.model.Report;
import com.proy.mainbank.service.CreditProductService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/credit-products")
@AllArgsConstructor
public class CreditProductController {

    private final CreditProductService creditProductService;

    @GetMapping("/client/{clientId}")
    public Flux<CreditProduct> getCreditProductsByClient(@PathVariable Long clientId) {
        Client client = new Client(clientId); // Suponiendo que ya tienes un método para obtener un cliente por su ID
        return creditProductService.getCreditProductsByClient(client);
    }

    @GetMapping("/client/{clientId}/has-products")
    public Mono<Boolean> hasCreditProducts(@PathVariable Long clientId) {
        Client client = new Client(clientId); // Suponiendo que ya tienes un método para obtener un cliente por su ID
        return creditProductService.hasCreditProducts(client);
    }

    @PostMapping("/payment")
    public Mono<Void> makePayment(@RequestBody CreditProduct creditProduct, @RequestParam double amount) {
        return creditProductService.makePayment(creditProduct, amount);
    }

    @GetMapping("/client/{clientId}/daily-average-balances")
    public Mono<Report> generateDailyAverageBalancesReport(@PathVariable Long clientId) {
        Client client = new Client(clientId); // Suponiendo que ya tienes un método para obtener un cliente por su ID
        return creditProductService.generateDailyAverageBalancesReport(client);
    }

    @GetMapping("/client/{clientId}/commissions-report")
    public Mono<Report> generateCommissionsReport(@PathVariable Long clientId) {
        Client client = new Client(clientId); // Suponiendo que ya tienes un método para obtener un cliente por su ID
        return creditProductService.generateCommissionsReport(client);
    }
}