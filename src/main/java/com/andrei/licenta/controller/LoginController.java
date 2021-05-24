package com.andrei.licenta.controller;

import com.andrei.licenta.exceptions.ExceptionHandling;
import com.andrei.licenta.model.user.AppUser;
import com.andrei.licenta.model.user.User;
import com.andrei.licenta.security.jwt.JwtTokenProvider;
import com.andrei.licenta.service.user.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

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

//    private HttpHeaders getJwtHeader(AppUser appUser) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(appUser));
//        return headers;
//    }

    private String getJwtHeaderString(AppUser appUser) {
        HttpHeaders headers = new HttpHeaders();
        return jwtTokenProvider.generateJwtToken(appUser);
    }

    private void authenticate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }
}
