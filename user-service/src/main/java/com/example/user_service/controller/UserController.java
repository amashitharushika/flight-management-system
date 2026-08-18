package com.example.user_service.controller;

import com.example.user_service.dto.*;
import com.example.user_service.model.User;
import com.example.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // HELPER METHOD: Ensures all details (including email) are sent to the frontend
    private Map<String, Object> buildProfileResponse(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("name", user.getName());
        map.put("email", user.getEmail()); // The missing piece!
        map.put("apiKey", user.getApiKey());
        return map;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request);
            // Use the helper method here
            return ResponseEntity.ok(buildProfileResponse(user));
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
                // Use the helper method here
                .map(u -> ResponseEntity.ok(buildProfileResponse(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody RegisterRequest request) {
        try {
            User updated = userService.updateUser(id, request);
            // Use the helper method here
            return ResponseEntity.ok(buildProfileResponse(updated));
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
            // Use the helper method here
            .map(u -> ResponseEntity.ok(buildProfileResponse(u)))
            .orElse(ResponseEntity.notFound().build());
    }

    private Object errorBody(String message) {
        return "{\"error\": \"" + message + "\"}";
    }
}