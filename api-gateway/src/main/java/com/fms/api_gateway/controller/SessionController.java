package com.fms.api_gateway.controller;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Exposes the currently authenticated Google identity to the frontend.
 * Handled directly by the Gateway (not proxied to a backend) — this is
 * how the client resolves "who am I" after the OAuth2/BFF login flow,
 * since the browser never sees the real Google token itself.
 */
@RestController
public class SessionController {

    @GetMapping("/api/session/me")
    public Mono<Map<String, Object>> me() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .map(auth -> {
                    OAuth2User user = (OAuth2User) auth.getPrincipal();
                    return Map.<String, Object>of(
                            "email", user.getAttribute("email") == null ? "" : user.getAttribute("email"),
                            "name", user.getAttribute("name") == null ? "" : user.getAttribute("name"),
                            "picture", user.getAttribute("picture") == null ? "" : user.getAttribute("picture")
                    );
                });
    }
}
