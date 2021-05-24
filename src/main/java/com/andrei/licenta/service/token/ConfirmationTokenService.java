package com.andrei.licenta.service.token;

import com.andrei.licenta.model.ConfirmationToken;
import com.andrei.licenta.repository.token.ConfirmationTokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;


    public ConfirmationTokenService(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;

    }

    public ConfirmationToken getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    public void setConfirmedAt(String token) {
        confirmationTokenRepository.updateConfirmedAt(
                token, LocalDate.now());
    }
}
