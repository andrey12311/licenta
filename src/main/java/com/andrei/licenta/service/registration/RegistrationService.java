package com.andrei.licenta.service.registration;

import com.andrei.licenta.exceptions.EmailExistsException;
import com.andrei.licenta.exceptions.TokenAlreadyConfirmedException;
import com.andrei.licenta.exceptions.TokenExpiredException;
import com.andrei.licenta.model.user.User;

import javax.mail.MessagingException;

public interface RegistrationService {
    User register(String firstName, String lastName, String email, String password) throws
            EmailExistsException, MessagingException;

    void confirmToken(String token) throws TokenAlreadyConfirmedException, TokenExpiredException;

    User createAdmin();
}
