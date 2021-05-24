package com.andrei.licenta.security.filter;

import com.andrei.licenta.security.jwt.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.andrei.licenta.constants.SecurityConstants.OPTIONS_HTTP_METHOD;
import static com.andrei.licenta.constants.SecurityConstants.TOKEN_PREFIX;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthorizationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        //check if is option
        if (request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)) {
            //if is option don't to anything
            response.setStatus(HttpStatus.OK.value());
        } else {
            //get the header for authorization
            //String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            Cookie[] cookies = request.getCookies();

            if (cookies != null) {
                String token = getCookie(cookies);

                if (token == null) {
                    filterChain.doFilter(request, response);
                    SecurityContextHolder.clearContext();
                    return;
                }

                //get the token and remove the "bearer" and we re left with the actual token
//                String token = token.substring(TOKEN_PREFIX.length());

                //once we have the token we can attempt to authenticate
                String username = jwtTokenProvider.getSubject(token);
                //if token is valid and user is not already authenticated
                if (jwtTokenProvider.isTokenValid(username, token) &&
                        SecurityContextHolder.getContext().getAuthentication() == null) {
                    List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);
                    Authentication authentication = jwtTokenProvider.getAuthentication(username, authorities, request);

                    //tell spring that the user is now an authenticated user
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    SecurityContextHolder.clearContext();
                }
            }
        }

        //we let the request continute it's course
        filterChain.doFilter(request, response);
    }

    private String getCookie(Cookie[] cookie){
        for(Cookie c  : cookie){
            if(c.getName().equals("sessionToken")){
                return c.getValue();
            }
        }
        return null;
    }
}
