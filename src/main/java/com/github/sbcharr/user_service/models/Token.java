package com.github.sbcharr.user_service.models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@Entity(name = "tokens")
public class Token extends BaseEntity {
    private String token;
    private Instant expiration;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    //@Enumerated(EnumType.STRING)
//    private TokenStatus tokenStatus;
}
