package com.andrei.licenta.listener;

import com.andrei.licenta.model.user.AppUser;
import com.andrei.licenta.service.LoginAttemptService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener {
    private final LoginAttemptService loginAttemptService;


    public AuthenticationSuccessListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent e) {
        //am nevoie de user pentru ca atunci cand te loghezi, metoda loadUserByUsername intoarce un UserPrincipal
        Object principal = e.getAuthentication().getPrincipal();

        if (principal instanceof AppUser) {
            AppUser user = (AppUser) e.getAuthentication().getPrincipal();
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }
}
