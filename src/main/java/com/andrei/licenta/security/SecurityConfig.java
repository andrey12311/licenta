package com.andrei.licenta.security;

import com.andrei.licenta.constants.SecurityConstants;
import com.andrei.licenta.security.filter.JWTAuthenticationEntryPoint;
import com.andrei.licenta.security.filter.JwtAccesDeniedHandler;
import com.andrei.licenta.security.filter.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final JwtAccesDeniedHandler jwtAccesDeniedHandler;
    private final JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public SecurityConfig(JwtAuthorizationFilter jwtAuthorizationFilter, JwtAccesDeniedHandler jwtAccesDeniedHandler,
                          JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          @Qualifier("UserDetailsService") UserDetailsService userDetailsService,
                          BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
        this.jwtAccesDeniedHandler = jwtAccesDeniedHandler;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().cors().and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)//nu tinem userul logat(stateless)
                .and().authorizeRequests().antMatchers(SecurityConstants.PUBLIC_URLS)//pentru url-urile astea nu e nevoie de autentificare
                .permitAll().anyRequest().authenticated() //daca nu e in public_urls atunci e nevoie de autentificare
                .and().exceptionHandling().accessDeniedHandler(jwtAccesDeniedHandler) //le arat jwtAccessDenied atunci cand nu au voie sa intre
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and().addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
