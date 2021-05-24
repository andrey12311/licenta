package com.andrei.licenta.service.user;

import com.andrei.licenta.exceptions.EmailExistsException;
import com.andrei.licenta.exceptions.EmailNotFoundException;
import com.andrei.licenta.exceptions.TokenAlreadyConfirmedException;
import com.andrei.licenta.exceptions.TokenExpiredException;
import com.andrei.licenta.model.Anunt;
import com.andrei.licenta.model.user.User;

import javax.mail.MessagingException;
import java.util.List;

public interface UserService {
    User findUserByEmail(String email);

    void resetPassword(String email) throws MessagingException, EmailNotFoundException;

    int enableAppUser(String email);

    void updateEmail(String oldEmail,String newEmail)
            throws EmailNotFoundException, MessagingException, EmailExistsException;
    void confirmEmail(String token ,String newEmail,String oldEmail)
            throws TokenAlreadyConfirmedException, TokenExpiredException;
    List<Anunt> getAnunturi(User user);
}
