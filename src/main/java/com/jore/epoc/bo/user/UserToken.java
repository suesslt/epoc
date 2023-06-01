package com.jore.epoc.bo.user;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.jore.jpa.BusinessObject;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
public class UserToken extends BusinessObject {
    private static final long ONE_DAY = 24 * 60 * 60;
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    private User user;
    @NotNull
    @NotEmpty
    private String token;
    private LocalDateTime expiryDate;

    public void calculateAndSetExpiryDate() {
        expiryDate = LocalDateTime.now().plus(ONE_DAY, ChronoUnit.SECONDS);
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
