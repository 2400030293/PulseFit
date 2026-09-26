package com.pulsefit.gateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class JwtGatewayFilter {

    private final String secret =
            "PulseFitSecretKey-ChangeThisInProduction-2026-123456789";

    public GatewayFilter filter() {

        return (exchange, chain) -> {

            String path = exchange.getRequest().getURI().getPath();

            // Login/Register do not need JWT
            if (path.startsWith("/api/auth/")) {
                return chain.filter(exchange);
            }

            String header = exchange.getRequest()
                    .getHeaders()
                    .getFirst("Authorization");

            if (header == null || !header.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            try {

                Claims claims = Jwts.parser()
                        .verifyWith(Keys.hmacShaKeyFor(
                                secret.getBytes(StandardCharsets.UTF_8)))
                        .build()
                        .parseSignedClaims(header.substring(7))
                        .getPayload();

                String username = claims.getSubject();
                String role = claims.get("role", String.class);

                // Add user information to the request
                exchange = exchange.mutate()
                        .request(exchange.getRequest()
                                .mutate()
                                .header("X-Username", username)
                                .header("X-Role", role)
                                .build())
                        .build();

                return chain.filter(exchange);

            } catch (Exception e) {

                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }
}