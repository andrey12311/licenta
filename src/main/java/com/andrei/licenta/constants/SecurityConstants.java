package com.andrei.licenta.constants;

public class SecurityConstants {
    public static final long EXPIRATION_TIME = 432_000_000;// 5 days expressed in milliseconds
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token"; //headeru pe care il folosim
    public static final String TOKEN_CANNOT_BE_VERIFIED =
            "Token cannot be verified"; //daca nu se poate verifica tokenu//trimit mesajul asta

    public static final String ISSUER = "Boncea Andrei"; //cine da tokenu
    public static final String AUDIENCE = "Licenta"; //cine va folosi tokenu
    public static final String AUTHORITIES = "Authorities"; //toate authorities are userului
    public static final String FORBIDDEN_MESSAGE = "You need to log in to access this page";
    public static final String ACCESS_DENIED = "You do not have permission to access this page";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String[] PUBLIC_URLS =
            {"/login-api/login","/register-api/*","/anunturi-api/anunturi",
                    "/anunturi-api/anunturi/*", "/anunturi-api/image/**"};
//public static final String[] PUBLIC_URLS = {"**"};
}
