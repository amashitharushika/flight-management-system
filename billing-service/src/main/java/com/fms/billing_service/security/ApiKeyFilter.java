package main.java.com.fms.billing_service.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class ApiKeyFilter implements Filter {

    @Value("${booking.api.key}")
    private String validApiKey;

    private static final String HEADER_NAME = "X-API-KEY";

@Override
public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
        throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    // allow the local UI to call this API from a different origin
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    response.setHeader("Access-Control-Allow-Headers", "X-API-KEY, Content-Type");

    // browsers send a preflight OPTIONS request before the real one — let it through
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
        response.setStatus(HttpServletResponse.SC_OK);
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