package org.example.ticketSalesSystem.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.ticketSalesSystem.Model.User;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET =
            "***REMOVED***";

    private final SecretKey key = Keys.hmacShaKeyFor(
            SECRET.getBytes(StandardCharsets.UTF_8)
    );

    public String generateToken(User user) {

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(key)
                .compact();
    }
}