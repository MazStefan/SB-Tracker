package com.github.mazstefan.sb_tracker.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // TODO: Move to application.properties after not hardcoded
    private final String SECRET_KEY = "MySuperSecretKeyForBudgetTrackerAppMustBeLongEnough";

    // 24 hours in milliseconds
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)  // Primary identifier
                .setIssuedAt(new Date(System.currentTimeMillis())) // Time when created
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Time when it expires
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Encryption algorithm
                .compact(); // Build into one String
    }

    public String extractMail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, String userEmail) {
        final String extractedEmail = extractMail(token);
        return (extractedEmail.equals(userEmail) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claimsResolver.apply(claims);
    }
}
