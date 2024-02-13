package com.proy.mainbank.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Report {


        private LocalDateTime generationDate;
        private Client client;
        private String reportType;
        private List<Object> reportData; // Datos del informe

        public Report() {
                // Puedes inicializar los atributos si es necesario
        }

}
