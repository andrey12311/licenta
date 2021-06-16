package com.andrei.licenta.service.user;

import com.andrei.licenta.constants.Authority;
import com.andrei.licenta.enumeration.Role;
import com.andrei.licenta.exceptions.*;
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
import java.util.Optional;
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
        if (user.getIsNonLocked()) {
            if (loginAttemptService.hasExceededMaxAttempts(user.getEmail())) {
                //blocam contu
                user.setIsNonLocked(false);
            } else {
                //deblocam contu
                user.setIsNonLocked(true);
            }
        } else {
            //daca e deja locked atunci ii dam remove din cache pt ca nu mai e nevoie de el
            loginAttemptService.evictUserFromLoginAttemptCache(user.getEmail());
        }
    }


    @Override
    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    public int enableAppUser(String email) {
        return userRepository.enableAppUser(email);
    }

    @Override
    public List<Anunt> getAnunturi(User user) {
        return anuntRepository.getAnunturileMele(user);
    }

    @Override
    public Long countUsers() {
        return this.userRepository.count();
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return this.userRepository.findById(id);
    }

    @Override
    public User update(String id, String firstName, String lastName, String email, Boolean isNonLocked)
            throws UserNotFoundException, MessagingException, EmailExistsException {
        User user = findUserById(Long.parseLong(id)).orElseThrow(
                ()->new UserNotFoundException("Utilizatorul nu a fost gasit"));
        //daca userul si-a schimbat mailul
        if(!user.getEmail().equals(email)){
            User existingUser = userRepository.findUserByEmail(email);
            //verificam daca exista deja mailul
            if(existingUser == null){
                //trimitem mail de verificare si dezactivam contul
                String token = UUID.randomUUID().toString();
                ConfirmationToken confirmationToken =
                        new ConfirmationToken(token, LocalDate.now(), LocalDate.now().plusDays(10), user);
                confirmationTokenService.saveConfirmationToken(confirmationToken);
                String link = "http://localhost:4200/confirmEmail?token=" + token;
                user.setIsActive(false);
                user.setEmail(email);
                emailService.sendEmail(user.getFirstName(),email,link);
            }else{
                throw new EmailExistsException("Mail deja existent");
            }
        }
        user.setFirstName(firstName);
        System.out.println(isNonLocked);
        user.setIsNonLocked(isNonLocked);
        user.setLastName(lastName);

        userRepository.save(user);

        return user;
    }


    @Override
    public User deleteAccount(User user) throws MessagingException, UserNotFoundException {
        User u  = findUserById(user.getId()).orElseThrow(()-> new UserNotFoundException("User inexistent"));
        emailService.sendEmail("bonceaandrei2000@gmail.com","Utilizatorul cu email-ul " +
                user.getEmail() +" doreste sa-si inchida contul.");

        return u;
    }


}
