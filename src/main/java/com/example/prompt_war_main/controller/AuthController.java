package com.example.prompt_war_main.controller;

import com.example.prompt_war_main.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final JwtService jwtService;

    @Value("${auth.admin.username}")
    private String adminUsername;

    @Value("${auth.admin.password}")
    private String adminPassword;

    @Autowired
    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping("/token")
    public ResponseEntity<Map<String, String>> getToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header("WWW-Authenticate", "Basic realm=\"API\"")
                    .body(Map.of("error", "Basic authentication is required"));
        }

        try {
            String base64Credentials = authHeader.substring(6).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);

            // credentials is in form username:password
            String[] values = credentials.split(":", 2);
            if (values.length == 2 && adminUsername.equals(values[0]) && adminPassword.equals(values[1])) {
                String token = jwtService.generateToken("admin");
                return ResponseEntity.ok(Map.of(
                        "token", token,
                        "type", "Bearer",
                        "expiresIn", "3600"
                ));
            }
        } catch (Exception e) {
            // Fall through to unauthorized
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid username or password"));
    }
}
