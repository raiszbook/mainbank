package com.proy.mainbank.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private ClientType clientType;
    private String contactInfo;

    @OneToMany(mappedBy = "client")
    private List<BankAccount> bankaccounts;

    @OneToMany(mappedBy = "client")
    private List<CreditProduct> creditproducts;

    @OneToMany(mappedBy = "client")
    private List<Card> cards;


    public Client() {
        // Puedes inicializar los atributos si es necesario
    }

    // Constructor
    public Client(Long id, String name, ClientType clientType, String contactInfo) {
        this.id = id;
        this.name = name;
        this.clientType = clientType;
        this.contactInfo = contactInfo;
    }

    public Client(Long id) {
        this.id = id;
    }

}
