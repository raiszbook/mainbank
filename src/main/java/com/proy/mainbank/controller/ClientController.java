package com.proy.mainbank.controller;

import com.proy.mainbank.model.BankAccount;
import com.proy.mainbank.model.Card;
import com.proy.mainbank.model.Client;
import com.proy.mainbank.model.ClientType;
import com.proy.mainbank.service.ClientService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/clients")
@AllArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/client-type/{clientType}")
    public Flux<Client> getClientsByClientType(@PathVariable ClientType clientType) {
        return clientService.getClientsByClientType(clientType);
    }

    @GetMapping("/name/{name}")
    public Flux<Client> getClientsByName(@PathVariable String name) {
        return clientService.getClientsByName(name);
    }

    @GetMapping("/contact-info/{contactInfo}")
    public Mono<Client> getClientByContactInfo(@PathVariable String contactInfo) {
        return clientService.getClientByContactInfo(contactInfo);
    }

    @GetMapping("/{clientId}/has-credit-products")
    public Mono<Boolean> hasCreditProducts(@PathVariable Long clientId) {
        Client client = new Client(clientId); // Suponiendo que ya tienes un método para obtener un cliente por su ID
        return clientService.hasCreditProducts(client);
    }

    @PostMapping("/associate-debit-card")
    public Mono<Void> associateDebitCardToBankAccount(@RequestBody Client client, @RequestBody Card debitCard, @RequestBody BankAccount bankAccount) {
        return clientService.associateDebitCardToBankAccount(client, debitCard, bankAccount);
    }
}