package com.andrei.licenta.service.user;

import com.andrei.licenta.exceptions.EmailExistsException;
import com.andrei.licenta.exceptions.EmailNotFoundException;
import com.andrei.licenta.exceptions.TokenAlreadyConfirmedException;
import com.andrei.licenta.exceptions.TokenExpiredException;
import com.andrei.licenta.model.Anunt;
import com.andrei.licenta.model.ConfirmationToken;
import com.andrei.licenta.model.user.AppUser;
import com.andrei.licenta.model.user.User;
import com.andrei.licenta.repository.anunt.AnuntRepository;
import com.andrei.licenta.repository.user.UserRepository;
import com.andrei.licenta.service.LoginAttemptService;
import com.andrei.licenta.service.email.EmailService;
import com.andrei.licenta.service.token.ConfirmationTokenService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Qualifier("UserDetailsService")
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {
    private static final String NO_USER_FOUND_BY_EMAIL = "Nu s-a gasit nici un utilizatorul cu mailul ";
    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;
    private final EmailService emailService;
    private final ConfirmationTokenService confirmationTokenService;
    private final AnuntRepository anuntRepository;

    public UserServiceImpl(UserRepository userRepository,
                           LoginAttemptService loginAttemptService, EmailService emailService,
                           ConfirmationTokenService confirmationTokenService, AnuntRepository anuntRepository) {
        this.userRepository = userRepository;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;
        this.confirmationTokenService = confirmationTokenService;
        this.anuntRepository = anuntRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) {
        User user = userRepository.findUserByEmail(s);
        // userul nu e autentificat
        if (user == null) {
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_EMAIL + s);
        } else {//userul e autentificaticat
            validateLoginAttempt(user);
            userRepository.save(user);

            return new AppUser(user);
        }
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }


    @Override
    public void resetPassword(String email) throws MessagingException, EmailNotFoundException {
    }

    private void validateLoginAttempt(User user) {
        //daca nu e blocat
        if (user.isNonLocked()) {
            if (loginAttemptService.hasExceededMaxAttempts(user.getEmail())) {
                //blocam contu
                user.setNonLocked(false);
            } else {
                //deblocam contu
                user.setNonLocked(true);
            }
        } else {
            //daca e deja locked atunci ii dam remove din cache pt ca nu mai e nevoie de el
            loginAttemptService.evictUserFromLoginAttemptCache(user.getEmail());
        }
    }

    @Override
    public void updateEmail(String oldEmail,String newEmail)
            throws  MessagingException, EmailExistsException {
    }

    @Transactional
    public void confirmEmail(String token ,String newEmail,String oldEmail)
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

        userRepository.updateEmail(newEmail, oldEmail);
    }



    public int enableAppUser(String email) {
        return userRepository.enableAppUser(email);
    }

    private String createEmailLink(User user) {
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken =
                new ConfirmationToken(token, LocalDate.now(), LocalDate.now().plusDays(10), user);
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        return "http://localhost:8081/register/confirmEmail?token=" + token;
    }

    @Override
    public List<Anunt> getAnunturi(User user) {
        return anuntRepository.getAnunturileMele(user);
    }
}
