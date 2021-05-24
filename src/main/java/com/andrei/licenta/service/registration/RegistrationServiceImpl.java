package com.andrei.licenta.service.registration;

import com.andrei.licenta.enumeration.Role;
import com.andrei.licenta.exceptions.EmailExistsException;
import com.andrei.licenta.exceptions.TokenAlreadyConfirmedException;
import com.andrei.licenta.exceptions.TokenExpiredException;
import com.andrei.licenta.model.ConfirmationToken;
import com.andrei.licenta.model.HttpResponse;
import com.andrei.licenta.model.user.User;
import com.andrei.licenta.repository.user.UserRepository;
import com.andrei.licenta.service.email.EmailService;
import com.andrei.licenta.service.token.ConfirmationTokenService;
import com.andrei.licenta.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final UserService userService;

    public RegistrationServiceImpl(UserRepository userRepository, EmailService emailService,
                                   BCryptPasswordEncoder passwordEncoder,
                                   ConfirmationTokenService confirmationTokenService,UserService userService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.confirmationTokenService = confirmationTokenService;
        this.userService = userService;

    }

    @Override
    public User register(String firstName, String lastName, String email, String password)
            throws EmailExistsException, MessagingException {

        User existingUser = userRepository.findUserByEmail(email);
        if (existingUser != null) {
            throw new EmailExistsException("Mail deja folosit");
        }
        User user = new User();

        String encodedPassword = encodePassword(password);

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setActive(false);
        user.setNonLocked(true);
        user.setRole(Role.ROLE_USER.name());
        user.setAuthorities(Role.ROLE_USER.getAuthorities());
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken =
                new ConfirmationToken(token, LocalDate.now(), LocalDate.now().plusDays(10), user);
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        String link = "http://localhost:4200/confirmEmail?token=" + token;
//        String link = "http://localhost:8081/register-api/confirm?token=" + token;

        emailService.sendEmail(firstName, email, link);

        return user;
    }

    @Transactional
    public void confirmToken(String token)
            throws TokenAlreadyConfirmedException, TokenExpiredException {

        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token);
        if (confirmationToken.getConfirmedAt() != null) {
            throw new TokenAlreadyConfirmedException("");
        }

        LocalDate expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDate.now())) {
            throw new TokenExpiredException("");
        }

        confirmationTokenService.setConfirmedAt(token);
        userService.enableAppUser(confirmationToken.getUser().getEmail());

    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

}
