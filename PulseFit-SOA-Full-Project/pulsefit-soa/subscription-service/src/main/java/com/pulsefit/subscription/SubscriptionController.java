package com.pulsefit.subscription;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api")
public class SubscriptionController {

    private final PlanRepository plans;
    private final SubscriptionRepository subs;
    private final RestClient.Builder rest;

    public SubscriptionController(
            PlanRepository p,
            SubscriptionRepository s,
            RestClient.Builder r) {

        plans = p;
        subs = s;
        rest = r;
    }

    // GET ALL PLANS
    @GetMapping("/plans")
    public List<Plan> plans() {
        return plans.findAll();
    }

    // CREATE PLAN
    @PostMapping("/plans")
    public ResponseEntity<Plan> createPlan(
            @RequestBody Plan p) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(plans.save(p));
    }

    // GET ALL SUBSCRIPTIONS - ADMIN
    @GetMapping("/subscriptions")
    public List<Subscription> allSubscriptions() {
        return subs.findAll();
    }

    // GET SUBSCRIPTIONS OF ONE MEMBER
    @GetMapping("/subscriptions/member/{memberId}")
    public List<Subscription> byMember(
            @PathVariable Long memberId) {

        return subs.findByMemberId(memberId);
    }

    // CREATE SUBSCRIPTION
    @PostMapping("/subscriptions")
    public ResponseEntity<?> subscribe(
            @RequestBody Map<String, Object> body) {

        try {

            Long memberId =
                    ((Number) body.get("memberId")).longValue();

            Long planId =
                    ((Number) body.get("planId")).longValue();

            if (memberId == null || planId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "message",
                                "memberId and planId required"
                        ));
            }

            // CHECK MEMBER
            try {

                rest.build()
                        .get()
                        .uri(
                                "http://MEMBER-SERVICE/api/members/{id}",
                                memberId
                        )
                        .retrieve()
                        .toBodilessEntity();

            } catch (Exception e) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "message",
                                "member does not exist or Member Service is unavailable"
                        ));
            }

            // CHECK PLAN
            Plan p = plans.findById(planId).orElse(null);

            if (p == null) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "message",
                                "plan does not exist"
                        ));
            }

            // START DATE
            LocalDate startDate = LocalDate.now();

            if (body.get("startDate") != null &&
                    !body.get("startDate").toString().isBlank()) {

                startDate = LocalDate.parse(
                        body.get("startDate").toString()
                );
            }

            // END DATE
            LocalDate expiryDate =
                    startDate.plusMonths(p.getDurationMonths());

            if (body.get("expiryDate") != null &&
                    !body.get("expiryDate").toString().isBlank()) {

                expiryDate = LocalDate.parse(
                        body.get("expiryDate").toString()
                );
            }

            if (!expiryDate.isAfter(startDate)) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "message",
                                "expiry date must be after start date"
                        ));
            }

            // PAYMENT STATUS
            String paymentStatus =
                    body.get("paymentStatus") != null
                            ? body.get("paymentStatus").toString()
                            : "PENDING";

            // PAYMENT METHOD
            String paymentMethod =
                    body.get("paymentMethod") != null
                            ? body.get("paymentMethod").toString()
                            : null;

            // PAYMENT AMOUNT
            Double paymentAmount = p.getPrice();

            if (body.get("paymentAmount") != null) {

                paymentAmount =
                        ((Number) body.get("paymentAmount"))
                                .doubleValue();
            }

            // PAYMENT DATE
            LocalDate paymentDate = null;

            if ("PAID".equalsIgnoreCase(paymentStatus)) {

                paymentDate = LocalDate.now();

                if (body.get("paymentDate") != null &&
                        !body.get("paymentDate").toString().isBlank()) {

                    paymentDate = LocalDate.parse(
                            body.get("paymentDate").toString()
                    );
                }
            }

            // CREATE SUBSCRIPTION
            Subscription s = new Subscription();

            s.setMemberId(memberId);
            s.setPlanId(planId);
            s.setStartDate(startDate);
            s.setExpiryDate(expiryDate);
            s.setStatus("ACTIVE");

            s.setPaymentStatus(
                    paymentStatus.toUpperCase()
            );

            s.setPaymentAmount(paymentAmount);
            s.setPaymentDate(paymentDate);
            s.setPaymentMethod(paymentMethod);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(subs.save(s));

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Invalid subscription data"
                    ));
        }
    }

    // RENEW SUBSCRIPTION
    @PutMapping("/subscriptions/{id}/renew")
    public ResponseEntity<?> renew(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        return subs.findById(id)
                .map(s -> {

                    try {
                        String start = body.get("startDate");
                        String end = body.get("expiryDate");

                        if (start == null || start.isBlank() ||
                                end == null || end.isBlank()) {

                            return ResponseEntity.badRequest()
                                    .body(Map.of(
                                            "message",
                                            "startDate and expiryDate are required"
                                    ));
                        }

                        LocalDate startDate =
                                LocalDate.parse(start);

                        LocalDate expiryDate =
                                LocalDate.parse(end);

                        if (!expiryDate.isAfter(startDate)) {

                            return ResponseEntity.badRequest()
                                    .body(Map.of(
                                            "message",
                                            "expiry date must be after start date"
                                    ));
                        }

                        s.setStartDate(startDate);
                        s.setExpiryDate(expiryDate);
                        s.setStatus("ACTIVE");

                        return ResponseEntity.ok(
                                subs.save(s)
                        );

                    } catch (Exception e) {

                        return ResponseEntity.badRequest()
                                .body(Map.of(
                                        "message",
                                        "Invalid renewal dates"
                                ));
                    }
                })
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }

    // UPDATE PAYMENT STATUS
    @PutMapping("/subscriptions/{id}/payment")
    public ResponseEntity<?> updatePayment(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        return subs.findById(id)
                .map(s -> {

                    String paymentStatus =
                            body.get("paymentStatus");

                    String paymentMethod =
                            body.get("paymentMethod");

                    if (paymentStatus == null ||
                            paymentStatus.isBlank()) {

                        return ResponseEntity.badRequest()
                                .body(Map.of(
                                        "message",
                                        "paymentStatus is required"
                                ));
                    }

                    s.setPaymentStatus(
                            paymentStatus.toUpperCase()
                    );

                    if (paymentMethod != null &&
                            !paymentMethod.isBlank()) {

                        s.setPaymentMethod(paymentMethod);
                    }

                    if ("PAID".equalsIgnoreCase(paymentStatus)) {

                        s.setPaymentDate(LocalDate.now());

                    } else {

                        s.setPaymentDate(null);
                    }

                    return ResponseEntity.ok(
                            subs.save(s)
                    );

                })
                .orElse(
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }
}