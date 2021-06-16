package com.andrei.licenta.controller;

import com.andrei.licenta.exceptions.*;
import com.andrei.licenta.model.HttpResponse;
import com.andrei.licenta.model.user.AppUser;
import com.andrei.licenta.model.user.User;
import com.andrei.licenta.security.jwt.JwtTokenProvider;
import com.andrei.licenta.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RequestMapping("/login-api")
@RestController
public class LoginController extends ExceptionHandling {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginController(UserService userService,
                           AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user, HttpServletResponse response) {
        authenticate(user.getEmail(), user.getPassword());
        User loginUser = userService.findUserByEmail(user.getEmail());
        AppUser appUser = new AppUser(loginUser);
        Cookie cookie = new Cookie("sessionToken",
                getJwtHeaderString(appUser));
        cookie.setHttpOnly(true);
        cookie.setMaxAge(600000);
        cookie.setPath("/");
        response.addCookie(cookie);

        return new ResponseEntity<>(loginUser, OK);
    }


    @GetMapping("/logged-in")
    public ResponseEntity<User> loggedIn(HttpServletRequest request)
            throws SessionExpiredException{

        User authenticatedUser;
        if(request.getCookies() == null){
            throw new SessionExpiredException("Sesiunea a expirat, trebuie sa va autentificati");
        }
        String jwtToken = getCookie(request.getCookies());
        if(jwtToken == null ){
            throw new SessionExpiredException("Sesiunea a expirat, va rugan sa va autentificati");
        }

        String email = jwtTokenProvider.getSubject(jwtToken);
        authenticatedUser = userService.findUserByEmail(email);
        //if token is valid and user is not already authenticated
        if (jwtTokenProvider.isTokenValid(email, jwtToken) &&
                SecurityContextHolder.getContext().getAuthentication() == null) {
            List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(jwtToken);
            Authentication authentication = jwtTokenProvider.getAuthentication(email, authorities, request);

            //tell spring that the user is now an authenticated user
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            SecurityContextHolder.clearContext();
        }
        return new ResponseEntity<>(authenticatedUser,OK);
    }

    private String getCookie(Cookie[] cookie){
        for(Cookie c  : cookie){
            if(c.getName().equals("sessionToken")){
                return c.getValue();
            }

        }
        return null;
    }
    private String getJwtHeaderString(AppUser appUser) {
        return jwtTokenProvider.generateJwtToken(appUser);
    }

    private void authenticate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }
}
