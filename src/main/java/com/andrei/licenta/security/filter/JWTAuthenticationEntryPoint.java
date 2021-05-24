package com.andrei.licenta.security.filter;

import com.andrei.licenta.constants.SecurityConstants;
import com.andrei.licenta.model.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;

@Component
public class JWTAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException arg2) throws IOException {
        HttpResponse httpResponse = new HttpResponse(FORBIDDEN.value(), FORBIDDEN,
                FORBIDDEN.getReasonPhrase().toUpperCase(), SecurityConstants.FORBIDDEN_MESSAGE);

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(FORBIDDEN.value());
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, httpResponse);
        outputStream.flush();
    }
}
