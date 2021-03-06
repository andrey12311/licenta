package com.andrei.licenta.exceptions;

import com.andrei.licenta.model.HttpResponse;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ExceptionHandling implements ErrorController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String ACCOUNT_LOCKED = "Contul dumneavoastra a fost blocat, contactati administratorul";
    private static final String METHOD_IS_NOT_ALLOWED = "Metoda neautorizata, trimiteti o metoda '%s'";
    private static final String INTERNAL_SERVER_ERROR_MSG = "S-a intamplat o eroare la server";
    private static final String INCORRECT_CREDENTIALS = "Email sau parola incorecta";
    private static final String ACCOUNT_DISABLED = "Email-ul trebuie verificat!";
    private static final String ERROR_PROCESSING_FILE = "Fisierul nu s-a putut procesa";
    private static final String NOT_ENOUGH_PERMISSION = "Nu aveti destule perimisiuni ";
    private static final String ERROR_PATH = "/error";
    private static final String EMAIL_NOT_VERIFIED = "Email-ul dumneavoastra trebuie verificat pentru a accesa aplicatia!";

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> accountDisabledException(){
        return createHttpResponse(BAD_REQUEST,ACCOUNT_DISABLED);
    }



    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> badCredentialsException(){
        return createHttpResponse(BAD_REQUEST,INCORRECT_CREDENTIALS);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException(){
        return createHttpResponse(FORBIDDEN,NOT_ENOUGH_PERMISSION);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<HttpResponse> tokenExpiredException(TokenExpiredException exception){
        return createHttpResponse(BAD_REQUEST,exception.getMessage());
    }

    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<HttpResponse> emailExistsException(EmailExistsException exception){
        return createHttpResponse(BAD_REQUEST,exception.getMessage());
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<HttpResponse> emailNotFoundException(EmailNotFoundException exception){
        return createHttpResponse(BAD_REQUEST,exception.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
        return createHttpResponse(METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<HttpResponse> internalServerErrorException(Exception exception) {
//        LOGGER.error(exception.getMessage());
//        return createHttpResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
//    }


//    @ExceptionHandler(NotAnImageFileException.class)
//    public ResponseEntity<HttpResponse> notAnImageFileException(NotAnImageFileException exception) {
//        LOGGER.error(exception.getMessage());
//        return createHttpResponse(BAD_REQUEST, exception.getMessage());
//    }

    @ExceptionHandler(AnuntNotFoundException.class)
    public ResponseEntity<HttpResponse> anuntNotFoundException(Exception e){
        return createHttpResponse(NOT_FOUND,e.getMessage());
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<HttpResponse> notFoundException(NoResultException exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<HttpResponse> iOException(IOException exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse> lockedException() {
        return createHttpResponse(UNAUTHORIZED, ACCOUNT_LOCKED);
    }

    @ExceptionHandler(com.andrei.licenta.exceptions.TokenExpiredException.class)
    public ResponseEntity<HttpResponse> tokenExpiredException(){
        return createHttpResponse(BAD_REQUEST,"Token-ul a expirat");
    }

    @ExceptionHandler(TokenAlreadyConfirmedException.class)
    public ResponseEntity<HttpResponse> tokenAlreadyConfirmedException(){
        return createHttpResponse(BAD_REQUEST,"Acest email a fost deja verificat");
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpResponse> userNotFoundException(){
        return createHttpResponse(NOT_FOUND,"Utilizatorul nu a fost gasit");
    }

    @ExceptionHandler(SessionExpiredException.class)
    public ResponseEntity<HttpResponse> sessionExpiredException(){
        return createHttpResponse(BAD_REQUEST,"Sesiunea a expirat va rugam sa va autentificati.");
    }

    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus,
                httpStatus.getReasonPhrase().toUpperCase(), message), httpStatus);
    }

    @RequestMapping(ERROR_PATH)
    public ResponseEntity<HttpResponse> notFound404() {
        return createHttpResponse(NOT_FOUND, "There is no mapping for this URL");
    }
    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

}
