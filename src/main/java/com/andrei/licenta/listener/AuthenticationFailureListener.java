package com.andrei.licenta.listener;

import com.andrei.licenta.service.LoginAttemptService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener {
    private final LoginAttemptService loginAttemptService;

    public AuthenticationFailureListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @EventListener
    public void onAuthenticateFailure(AuthenticationFailureBadCredentialsEvent e) {
        Object principal = e.getAuthentication().getPrincipal();

        if (principal instanceof String) {
            String username = (String) e.getAuthentication().getPrincipal();
            loginAttemptService.addUserToLoginAttemptCache(username);
        }
    }
}
