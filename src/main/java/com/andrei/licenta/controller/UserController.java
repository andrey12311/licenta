package com.andrei.licenta.controller;

import com.andrei.licenta.exceptions.ExceptionHandling;
import com.andrei.licenta.exceptions.UserNotFoundException;
import com.andrei.licenta.model.Anunt;
import com.andrei.licenta.model.HttpResponse;
import com.andrei.licenta.model.user.User;
import com.andrei.licenta.security.jwt.JwtTokenProvider;
import com.andrei.licenta.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/user")
public class UserController extends ExceptionHandling {

    private final UserService userService;

    private final JwtTokenProvider jwtTokenProvider;

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/logout")
    public ResponseEntity<User> logOut(@RequestBody User user, HttpServletResponse response,
                                       HttpServletRequest request) {

        User loggedInUser = userService.findUserByEmail(user.getEmail());
        Cookie[] cookies = request.getCookies();
        Cookie cookie = cookies[0];
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return new ResponseEntity<>(loggedInUser, OK);
    }

    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus,
                httpStatus.getReasonPhrase().toUpperCase(), message), httpStatus);
    }

    @GetMapping("/anunturi/{userId}")
    public ResponseEntity<List<Anunt>> getAnunturi(@PathVariable("userId") User user) {
        List<Anunt> anunturileMele = userService.getAnunturi(user);

        return new ResponseEntity<>(anunturileMele, OK);
    }

    @PostMapping("/delete-account")
    public ResponseEntity<User> deleteAccount(@RequestBody User user)
            throws UserNotFoundException, MessagingException {

        User deletedUser = userService.deleteAccount(user);

        return new ResponseEntity<>(deletedUser,OK);
    }
}
