package com.javaspringboot.javaspringbootcore.app.auth.entity;

import com.javaspringboot.javaspringbootcore.app.auth.enums.AuthState;
import com.javaspringboot.javaspringbootcore.app.auth.enums.AuthType;
import com.javaspringboot.javaspringbootcore.app.user.entity.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "auth")
@Getter
@Setter
@NoArgsConstructor
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", referencedColumnName = "id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthState state = AuthState.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthType type;

    @Column(name = "sign_in_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date signInDate;

    @Column(name = "sign_out_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date signOutDate;

    @Column(name = "last_request_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastRequestDate;

    @Column(name = "expires_in")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiresIn;

    @Column(name = "token")
    private String token;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "invalid_token_count", nullable = false)
    private int invalidTokenCount = 0;
}