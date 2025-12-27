package com.paypal.reward_service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reward")
public class Reward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Double points;
    private LocalDateTime sentAt;

    @Column(unique = true)
    private Long transactionId;

    //getter setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Double getPoints() { return points; }
    public void setPoints(Double points) { this.points = points; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime timestamp) { this.sentAt = timestamp; }

    public void setTransactionId(Long id) { transactionId = id; }
}
