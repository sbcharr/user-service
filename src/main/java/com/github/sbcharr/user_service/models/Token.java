package com.github.sbcharr.user_service.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@Entity(name = "tokens")
public class Token extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String value;

    @Column(name = "expiry_at", nullable = false)
    private Instant expiryAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
}
