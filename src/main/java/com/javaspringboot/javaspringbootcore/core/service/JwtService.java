package com.javaspringboot.javaspringbootcore.core.service;

import com.javaspringboot.javaspringbootcore.app.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String SECRET_KEY;


    public String extractUsername(String token) {
        return exportToken(token, Claims::getSubject);
    }

    private <T> T exportToken(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = Jwts.parser()
                .verifyWith(getKey())
                .build().parseSignedClaims(token).getPayload();

        return claimsTFunction.apply(claims);
    }

    private SecretKey getKey() {
        byte[] key = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(key);
    }


    public boolean tokenControl(String jwt, String email) {
        final String userEmail = extractUsername(jwt);
        return (userEmail.equals(email) && !exportToken(jwt, Claims::getExpiration).before(new Date()));
    }

    public String generateToken(User user, Integer expiresIn) {
        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiresIn))
                .signWith(getKey())
                .compact();
    }
}
