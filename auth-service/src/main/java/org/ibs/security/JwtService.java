package org.ibs.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.ibs.util.Constants;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {


    private Key getSignKey() {
        return Keys.hmacShaKeyFor(Constants.SECRET.getBytes(StandardCharsets.UTF_8));
    }

    // Generate token
    public String generateToken(UserDetails userDetails) {

        String role = userDetails.getAuthorities()
                .stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("ROLE_USER");

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", List.of(role))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000)) // expire in 1 hour
                .signWith(getSignKey())
                .compact();
    }

    // Extract username
    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Validate Token
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // Check Token expiration

    public boolean isTokenExpired(String token) {

        return extractClaims(token)
                .getExpiration()
                .before(new Date());
    }
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}