package com.pulsefit.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository repo;
    private final RestClient.Builder rest;

    private final BCryptPasswordEncoder encoder =
            new BCryptPasswordEncoder();

    private final String secret =
            "PulseFitSecretKey-ChangeThisInProduction-2026-123456789";

    public AuthController(
            UserRepository repo,
            RestClient.Builder rest) {

        this.repo = repo;
        this.rest = rest;
    }

    // ================= REGISTER =================

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody Map<String, String> body) {

        String username = body.get("username");
        String password = body.get("password");

        String name = body.get("name");
        String email = body.get("email");
        String phone = body.get("phone");
        String facility = body.get("facility");

        if (username == null || password == null ||
                username.isBlank() || password.isBlank()) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "username and password are required"
                    ));
        }

        if (name == null || name.isBlank() ||
                email == null || email.isBlank() ||
                phone == null || phone.isBlank()) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "name, email and phone are required"
                    ));
        }

        if (password.length() < 6) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "password must be at least 6 characters"
                    ));
        }

        if (repo.existsByUsername(username)) {

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "message",
                            "username already exists"
                    ));
        }

        try {

            // Create Member first
            Map<String, Object> memberBody =
                    new HashMap<>();

            memberBody.put("name", name);
            memberBody.put("email", email);
            memberBody.put("phone", phone);
            memberBody.put(
                    "facility",
                    facility == null || facility.isBlank()
                            ? "Main Facility"
                            : facility
            );

            Map memberResponse =
                    rest.build()
                            .post()
                            .uri(
                                    "http://MEMBER-SERVICE/api/members"
                            )
                            .body(memberBody)
                            .retrieve()
                            .body(Map.class);

            if (memberResponse == null ||
                    memberResponse.get("id") == null) {

                return ResponseEntity
                        .status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(Map.of(
                                "message",
                                "Member Service could not create member"
                        ));
            }

            Long memberId =
                    ((Number) memberResponse.get("id"))
                            .longValue();

            // Create Auth User
            UserAccount u =
                    new UserAccount(
                            username,
                            encoder.encode(password)
                    );

            u.setMemberId(memberId);

            repo.save(u);

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "registered successfully",
                            "username",
                            username,
                            "memberId",
                            memberId
                    )
            );

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of(
                            "message",
                            "Unable to create member. Make sure Member Service is running."
                    ));
        }
    }

    // ================= LOGIN =================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> body) {

        UserAccount u = repo
                .findByUsername(body.get("username"))
                .orElse(null);

        if (u == null ||
                !encoder.matches(
                        body.get("password"),
                        u.getPassword())) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message",
                            "invalid credentials"
                    ));
        }

        String token = Jwts.builder()
                .subject(u.getUsername())
                .claim("role", u.getRole())
                .claim("memberId", u.getMemberId())
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 86400000
                        )
                )
                .signWith(
                        Keys.hmacShaKeyFor(
                                secret.getBytes(
                                        StandardCharsets.UTF_8
                                )
                        )
                )
                .compact();

        Map<String, Object> response =
                new HashMap<>();

        response.put("token", token);
        response.put(
                "username",
                u.getUsername()
        );
        response.put(
                "role",
                u.getRole()
        );
        response.put(
                "memberId",
                u.getMemberId()
        );

        return ResponseEntity.ok(response);
    }

    // ================= RESET PASSWORD =================

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody Map<String, String> body) {

        String username = body.get("username");
        String newPassword = body.get("newPassword");

        if (username == null || username.isBlank() ||
                newPassword == null || newPassword.isBlank()) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "username and newPassword are required"
                    ));
        }

        if (newPassword.length() < 6) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "password must be at least 6 characters"
                    ));
        }

        UserAccount u =
                repo.findByUsername(username)
                        .orElse(null);

        if (u == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message",
                            "username not found"
                    ));
        }

        u.setPassword(
                encoder.encode(newPassword)
        );

        repo.save(u);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "password reset successfully"
                )
        );
    }

    // ================= CURRENT USER =================

    @GetMapping("/me")
    public ResponseEntity<?> me(
            @RequestHeader(
                    value = "X-Username",
                    required = false
            ) String username) {

        if (username == null ||
                username.isBlank()) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of(
                                    "message",
                                    "User not identified"
                            )
                    );
        }

        return repo.findByUsername(username)

                .map(u -> {

                    Map<String, Object> response =
                            new HashMap<>();

                    response.put(
                            "username",
                            u.getUsername()
                    );

                    response.put(
                            "role",
                            u.getRole()
                    );

                    response.put(
                            "memberId",
                            u.getMemberId()
                    );

                    return ResponseEntity.ok(
                            response
                    );
                })

                .orElse(
                        ResponseEntity
                                .status(
                                        HttpStatus.NOT_FOUND
                                )
                                .body(
                                        Map.of(
                                                "message",
                                                "User not found"
                                        )
                                )
                );
    }
}