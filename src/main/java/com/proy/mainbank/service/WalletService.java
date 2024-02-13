package com.proy.mainbank.service;


import com.proy.mainbank.model.Wallet;
import com.proy.mainbank.repository.WalletRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@AllArgsConstructor
@Service
public class WalletService {

    private final WalletRepository walletRepository;


    public Mono<Object> registerYankiUser(Wallet user) {
        // Verificar si el usuario ya existe
        Mono<Wallet> existingUser = walletRepository.findByUserId(user.getId());
        return existingUser.flatMap(existing -> {
            // Si el usuario ya existe, lanzar una excepción o manejar el caso según sea necesario
            return Mono.error(new IllegalArgumentException("El usuario ya está registrado"));
        }).switchIfEmpty(
                // Si el usuario no existe, realizar más validaciones antes de guardar
                Mono.defer(() -> {
                    // Validar el formato del número de teléfono
                    String phoneNumber = user.getPhoneNumber();
                    if (!isValidPhoneNumber(phoneNumber)) {
                        return Mono.error(new IllegalArgumentException("Número de teléfono no válido"));
                    }
                    // Otros controles de validación aquí

                    // Guardar el usuario si pasa todas las validaciones
                    return walletRepository.save(user);
                })
        );
    }





    private boolean isValidPhoneNumber(String phoneNumber) {
        // Lógica de validación del número de teléfono aquí
        // Por ejemplo, validar el formato del número
        return phoneNumber.matches("\\d{10}"); // Por ejemplo, verificar si tiene 10 dígitos
    }

    public Mono<Void> associateDebitCardToYankiUser(Long userId, String debitCardNumber) {
        return walletRepository.findById(userId)
                .flatMap(user -> {
                    user.setDebitCardNumber(debitCardNumber);
                    return walletRepository.save(user).then();
                });
    }

    public Mono<Void> transferYankiBalance(Long sourceUserId, Long targetUserId, double amount) {
        BigDecimal transferAmount = BigDecimal.valueOf(amount);

        return walletRepository.findById(sourceUserId)
                .flatMap(sourceUser -> {
                    if (sourceUser.getBalance().compareTo(transferAmount) >= 0) {
                        // Actualizar el saldo del usuario origen
                        BigDecimal newBalance = sourceUser.getBalance().subtract(transferAmount);
                        sourceUser.setBalance(newBalance);
                        return walletRepository.save(sourceUser)
                                // Actualizar el saldo del usuario destino
                                .then(walletRepository.findById(targetUserId)
                                        .flatMap(targetUser -> {
                                            BigDecimal targetBalance = targetUser.getBalance().add(transferAmount);
                                            targetUser.setBalance(targetBalance);
                                            return walletRepository.save(targetUser).then();
                                        })
                                );
                    } else {
                        return Mono.error(new IllegalArgumentException("Insufficient funds for transfer"));
                    }
                });
    }

    public Mono<Double> getYankiBalance(Long userId) {
        return walletRepository.findById(userId)
                .map(wallet -> wallet.getBalance().doubleValue());
    }
}
