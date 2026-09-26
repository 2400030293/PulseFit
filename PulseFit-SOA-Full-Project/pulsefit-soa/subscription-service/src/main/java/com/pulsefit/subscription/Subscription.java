package com.pulsefit.subscription;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;
    private Long planId;

    private LocalDate startDate;
    private LocalDate expiryDate;

    private String status;

    private String paymentStatus;
    private Double paymentAmount;
    private LocalDate paymentDate;
    private String paymentMethod;

    public Subscription() {
    }

    public Long getId() {
        return id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long v) {
        memberId = v;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long v) {
        planId = v;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate v) {
        startDate = v;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate v) {
        expiryDate = v;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String v) {
        status = v;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String v) {
        paymentStatus = v;
    }

    public Double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(Double v) {
        paymentAmount = v;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate v) {
        paymentDate = v;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String v) {
        paymentMethod = v;
    }
}