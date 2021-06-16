package com.andrei.licenta.controller;

import com.andrei.licenta.exceptions.EmailExistsException;
import com.andrei.licenta.exceptions.ExceptionHandling;
import com.andrei.licenta.exceptions.TokenAlreadyConfirmedException;
import com.andrei.licenta.exceptions.TokenExpiredException;
import com.andrei.licenta.model.HttpResponse;
import com.andrei.licenta.model.user.User;
import com.andrei.licenta.service.registration.RegistrationService;
import com.andrei.licenta.service.token.ConfirmationTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/register-api")
public class RegisterController extends ExceptionHandling {

    private final RegistrationService registrationService;


    public RegisterController(RegistrationService registrationService,
                              ConfirmationTokenService confirmationTokenService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws MessagingException, EmailExistsException {
        User newUser = registrationService.register(user.getFirstName(), user.getLastName(), user.getEmail()
                , user.getPassword());

         registrationService.createAdmin();
        return new ResponseEntity<>(newUser, OK);
    }

    @GetMapping(path = "/confirm")
    public ResponseEntity<HttpResponse> confirm(@RequestParam("token") String token)
            throws TokenExpiredException, TokenAlreadyConfirmedException {
        registrationService.confirmToken(token);

        return createHttpResponse(OK, "Mailul a fost confirmat,va puteti autentifica.");
    }

    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus,
                httpStatus.getReasonPhrase().toUpperCase(), message), httpStatus);
    }

}
