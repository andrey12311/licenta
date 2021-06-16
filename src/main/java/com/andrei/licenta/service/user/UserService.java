package com.andrei.licenta.service.user;

import com.andrei.licenta.exceptions.*;
import com.andrei.licenta.model.Anunt;
import com.andrei.licenta.model.user.User;
import org.apache.juli.logging.Log;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User findUserByEmail(String email);

    void resetPassword(String email) throws MessagingException, EmailNotFoundException;

    int enableAppUser(String email);

    List<Anunt> getAnunturi(User user);

    List<User> findAll();

    Long countUsers();

    Optional<User> findUserById(Long id);

    User update(String id, String firstName, String lastName, String email, Boolean isNonLocked)
            throws UserNotFoundException, MessagingException, EmailExistsException;

    User deleteAccount(User user) throws MessagingException, UserNotFoundException;

}
