package com.proy.mainbank.service;


import com.proy.mainbank.model.BankAccount;
import com.proy.mainbank.model.Card;
import com.proy.mainbank.model.Client;
import com.proy.mainbank.model.ClientType;
import com.proy.mainbank.repository.CardRepository;
import com.proy.mainbank.repository.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@AllArgsConstructor
@Service
public class ClientService {


    private final ClientRepository clientRepository;
    private final CardRepository cardRepository;


    // Obtener clientes por tipo de cliente.
    public Flux<Client> getClientsByClientType(ClientType clientType) {
        return clientRepository.findByClientType(clientType);
    }

    // Obtener clientes por nombre.
    public Flux<Client> getClientsByName(String name) {
        return clientRepository.findByName(name);
    }

    // Obtener cliente por información de contacto.
    public Mono<Client> getClientByContactInfo(String contactInfo) {
        return clientRepository.findByContactInfo(contactInfo);
    }

    // Verificar si un cliente tiene productos de crédito asociados.
    public Mono<Boolean> hasCreditProducts(Client client) {
        return clientRepository.existsClientByIdAndCreditProductIsNotNull(client.getId());
    }

    // Asociar tarjetas de débito a las cuentas bancarias de un cliente.
    public Mono<Void> associateDebitCardToBankAccount(Client client, Card debitCard, BankAccount bankAccount) {
        if (!client.getBankaccounts().contains(bankAccount)) {
            return Mono.error(new IllegalArgumentException("The client does not own the specified bank account"));
        }
        debitCard.setClient(client);
        debitCard.setBankAccount(bankAccount);
        cardRepository.save(debitCard);
        return Mono.empty(); // Devuelve un Mono<Void> vacío
    }

}
