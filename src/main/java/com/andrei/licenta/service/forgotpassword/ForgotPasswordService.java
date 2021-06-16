package com.andrei.licenta.service.forgotpassword;

import com.andrei.licenta.exceptions.EmailNotFoundException;
import com.andrei.licenta.exceptions.TokenAlreadyConfirmedException;
import com.andrei.licenta.exceptions.TokenExpiredException;
import com.andrei.licenta.model.ConfirmationToken;
import com.andrei.licenta.model.user.User;

import javax.mail.MessagingException;

public interface ForgotPasswordService {

    User retrievePassword(String email) throws MessagingException, EmailNotFoundException;

    User updatePassword(String password,String email,String token) throws Exception;

    ConfirmationToken confirmToken(String token) throws TokenAlreadyConfirmedException, TokenExpiredException;
}
