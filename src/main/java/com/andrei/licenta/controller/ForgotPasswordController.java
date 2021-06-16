package com.andrei.licenta.controller;



import com.andrei.licenta.exceptions.EmailNotFoundException;
import com.andrei.licenta.exceptions.TokenAlreadyConfirmedException;
import com.andrei.licenta.exceptions.TokenExpiredException;
import com.andrei.licenta.model.ConfirmationToken;
import com.andrei.licenta.model.HttpResponse;
import com.andrei.licenta.model.user.User;
import com.andrei.licenta.service.forgotpassword.ForgotPasswordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

import static org.springframework.http.HttpStatus.OK;

@RequestMapping("/forgot-api")
@RestController
public class ForgotPasswordController {
    private final ForgotPasswordService forgotPasswordService;

    public ForgotPasswordController(ForgotPasswordService forgotPasswordService) {
        this.forgotPasswordService = forgotPasswordService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<User> forgotPassword(@RequestParam("email")String email)
            throws MessagingException, EmailNotFoundException {
        User user = forgotPasswordService.retrievePassword(email);
        System.out.println("am ajuns aici");
        return new ResponseEntity<>(user,OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<User> changePassword(@RequestParam("password")String password,
                                               @RequestParam("email")String email,
                                               @RequestParam("token")String token) throws Exception {

        User user = forgotPasswordService.updatePassword(password,email,token);

        return new ResponseEntity<>(user,OK);
    }

    @GetMapping(path = "/confirm")
    public ResponseEntity<ConfirmationToken> checkToken(@RequestParam("token") String token)
            throws TokenExpiredException, TokenAlreadyConfirmedException {
        ConfirmationToken confirmationToken = forgotPasswordService.confirmToken(token);

        return new ResponseEntity<>(confirmationToken,OK);
    }
}
