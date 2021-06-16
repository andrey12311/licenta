package com.andrei.licenta.service.forgotpassword;

import com.andrei.licenta.exceptions.EmailNotFoundException;
import com.andrei.licenta.exceptions.TokenAlreadyConfirmedException;
import com.andrei.licenta.exceptions.TokenExpiredException;
import com.andrei.licenta.model.ConfirmationToken;
import com.andrei.licenta.model.user.User;
import com.andrei.licenta.repository.token.ConfirmationTokenRepository;
import com.andrei.licenta.repository.user.UserRepository;
import com.andrei.licenta.service.email.EmailService;
import com.andrei.licenta.service.token.ConfirmationTokenService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService{
    private final UserRepository userRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public ForgotPasswordServiceImpl(UserRepository userRepository, ConfirmationTokenService confirmationTokenService,
                                     EmailService emailService, ConfirmationTokenRepository confirmationTokenRepository,
                                     BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.confirmationTokenService = confirmationTokenService;
        this.emailService = emailService;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User retrievePassword(String email) throws MessagingException, EmailNotFoundException {
        //CAUT USER DUPA MAIL
        User user = userRepository.findUserByEmail(email);
        if(user != null){ //DACA GASESC TRIMIT MAIL PENTRU SCHIMBARE DE MAIL SI INTORC USER
            String token = UUID.randomUUID().toString();
            ConfirmationToken confirmationToken =
                    new ConfirmationToken(token, LocalDate.now(), LocalDate.now().plusDays(10), user);
            confirmationTokenService.saveConfirmationToken(confirmationToken);
            String link = "http://localhost:4200/schimbare-parola?token=" + token;
            emailService.sendForgotPasswordMail(user.getEmail(),user.getFirstName(),link);

            return user;
        }else{ //ALTFEL ARUNC EXCEPTIE
            throw new EmailNotFoundException("Email-ul nu a fost gasit");
        }
    }


    @Override
    public User updatePassword(String password,String email,String token) throws Exception {
        // CAUT USERUL
        User user = userRepository.findUserByEmail(email);

        //DACA GASESI USER ATUNCI FAC UPDATE LA PAROLA SI RETURN USER
        if(user != null){
            String encryptedPassword = encodePassword(password);
            int i = userRepository.updatePassword(email,encryptedPassword);
            System.out.println(i);
            if(i == 1) {
                confirmationTokenRepository.updateConfirmedAt(token,LocalDate.now());
                return user;
            }else{//ALTFEL NU
                throw new Exception("S-a intamplat o eroare la srever");
            }
        }else{
            throw new EmailNotFoundException("Email-ul nu a fost gasit");
        }
    }

    @Override
    @Transactional
    public ConfirmationToken confirmToken(String token) throws TokenAlreadyConfirmedException, TokenExpiredException {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token);
        if(confirmationToken.getConfirmedAt() != null){
            throw new TokenAlreadyConfirmedException("Acest link nu mai exista");
        }
        LocalDate expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDate.now())) {
            throw new TokenExpiredException("Acest link nu mai exista");
        }
        return confirmationToken;
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
