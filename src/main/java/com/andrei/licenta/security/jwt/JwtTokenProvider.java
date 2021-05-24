package com.andrei.licenta.security.jwt;

import com.andrei.licenta.constants.SecurityConstants;
import com.andrei.licenta.model.user.AppUser;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.andrei.licenta.constants.SecurityConstants.*;


@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")

    private String secret;

    public String generateJwtToken(AppUser appUser){
    String[] claims = getClaimsFromUser(appUser);
    return JWT.create().withIssuer(ISSUER).withAudience(AUDIENCE)
            .withIssuedAt(new Date()).withSubject(appUser.getUsername())
            .withArrayClaim(AUTHORITIES,claims).withExpiresAt(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
            .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    public String[] getClaimsFromUser(AppUser appUser){
        List<String> authorites = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : appUser.getAuthorities()) {
            authorites.add(grantedAuthority.getAuthority());
        }
        return authorites.toArray(new String[0]);
    }

    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier;
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);
            verifier = JWT.require(algorithm).withIssuer(ISSUER)
                    .build();
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }
        return verifier;
    }

    //dupa ce avem toketul ii spunem lui spring ca userul este autentificat si sa proceseze request-ul

    public Authentication getAuthentication(String username, List<GrantedAuthority> authorities,
                                            HttpServletRequest request) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(username, null, authorities);
        token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        return token;
    }
    public List<GrantedAuthority> getAuthorities(String token) {
        String[] claims = getClaimsFromToken(token);
        assert claims != null;
        //parcurg arrayul si transform tot din string in SimpleGrantedAuthroty si le transfom intr-o lista
        return Arrays.stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    private String[] getClaimsFromToken(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
    }

    // vedem daca tokenul este valid

    public boolean isTokenValid(String username, String token) {
        JWTVerifier verifier = getJWTVerifier();
        return StringUtils.isNotEmpty(username) && !isTokenExpired(verifier,token);
    }

    //vedem daca e expirat
    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        Date expiration = verifier.verify(token).getExpiresAt();

        return expiration.before(new Date());
    }

    //vedem al cui este tokenu
    public String getSubject(String token) {
        JWTVerifier verifier = getJWTVerifier();

        return verifier.verify(token).getSubject();
    }
}
