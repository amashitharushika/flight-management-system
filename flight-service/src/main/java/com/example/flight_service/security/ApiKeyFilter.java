package com.example.flight_service.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApiKeyFilter implements Filter {

    @Value("${flight.api.key}")
    private String validApiKey;

    private static final String HEADER_NAME = "X-API-KEY";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // allow the H2 console and Swagger docs through without a key, for local debugging
        String uri = request.getRequestURI();
        if (uri.startsWith("/h2-console") || uri.startsWith("/swagger-ui") || uri.startsWith("/v3/api-docs")) {
            chain.doFilter(req, res);
            return;
        }

        String providedKey = request.getHeader(HEADER_NAME);
        if (providedKey == null || !providedKey.equals(validApiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Missing or invalid API key\"}");
            return;
        }
        chain.doFilter(req, res);
    }
}
