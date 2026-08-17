package com.example.user_service.controller;

import com.example.user_service.dto.*;
import com.example.user_service.model.User;
import com.example.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request);
            return ResponseEntity.ok(new LoginResponse(user.getId(), user.getName(), user.getApiKey()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(userService.login(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorBody(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        return userService.getById(id)
                .map(u -> ResponseEntity.ok(new LoginResponse(u.getId(), u.getName(), u.getApiKey())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody RegisterRequest request) {
        try {
            User updated = userService.updateUser(id, request);
            return ResponseEntity.ok(new LoginResponse(updated.getId(), updated.getName(), updated.getApiKey()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody(e.getMessage()));
        }
    }

    @GetMapping("/validate-key")
    public ResponseEntity<?> validateKey(@RequestParam String key) {
        boolean valid = userService.validateKey(key);
        return ResponseEntity.ok().body("{\"valid\": " + valid + "}");
    }

    @GetMapping("/by-email")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        return userService.getByEmail(email)
            .map(u -> ResponseEntity.ok(new LoginResponse(u.getId(), u.getName(), u.getApiKey())))
            .orElse(ResponseEntity.notFound().build());
    }

    private Object errorBody(String message) {
        return "{\"error\": \"" + message + "\"}";
    }
}