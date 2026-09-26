package com.pulsefit.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

@Configuration
public class GatewaySecurityConfig {

    private final JwtGatewayFilter jwt;

    public GatewaySecurityConfig(JwtGatewayFilter jwt) {
        this.jwt = jwt;
    }

    @Bean
    public RouteLocator securedRoutes(RouteLocatorBuilder b) {

        GatewayFilter filter = jwt.filter();

        return b.routes()
                .route("auth", r -> r.path("/api/auth/**")
                        .uri("lb://AUTH-SERVICE"))

                .route("members", r -> r.path("/api/members/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://MEMBER-SERVICE"))

                .route("plans", r -> r.path("/api/plans/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://SUBSCRIPTION-SERVICE"))

                .route("subscriptions", r -> r.path("/api/subscriptions/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://SUBSCRIPTION-SERVICE"))

                .route("attendance", r -> r.path("/api/attendance/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://ATTENDANCE-SERVICE"))

                .build();
    }
}