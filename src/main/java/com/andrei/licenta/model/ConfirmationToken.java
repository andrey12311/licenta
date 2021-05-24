package com.andrei.licenta.model;

import com.andrei.licenta.model.user.User;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
public class ConfirmationToken implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long tokenId;
    @Column(nullable = false)
    private String token;
    @Column(nullable = false)
    private LocalDate createdAt;
    @Column(nullable = false)
    private LocalDate expiresAt;
    private LocalDate confirmedAt;
    @ManyToOne
    @JoinColumn(
            nullable = false,
            name = "id"
    )
    private User user;

    public ConfirmationToken() {
    }

    public ConfirmationToken(String token, LocalDate createdAt, LocalDate expiresAt, User user) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.user = user;
    }

    public Long getTokenId() {
        return tokenId;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDate expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDate getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDate confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
