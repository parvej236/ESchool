package com.parvej.backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    // ── Fixed key from application.properties ──────────
    // Previously: random key generated on every restart
    // — this meant ALL tokens broke whenever the server restarted
    @Value("${jwt.secret}")
    private String secretKey;

    // ── Token lifetimes from application.properties ────
    @Value("${jwt.access-token-expiry}")
    private long accessTokenExpiry;   // 600000 ms = 10 minutes

    @Value("${jwt.refresh-token-expiry}")
    private long refreshTokenExpiry;  // 604800000 ms = 7 days

    // ── Key ────────────────────────────────────────────
    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ── Generate tokens ────────────────────────────────

    /**
     * Short-lived access token (10 min) — sent with every API request
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "access");
        return buildToken(claims, username, accessTokenExpiry);
    }

    /**
     * Long-lived refresh token (7 days) — used only to get a new access token
     */
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return buildToken(claims, username, refreshTokenExpiry);
    }

    private String buildToken(Map<String, Object> claims, String username, long expiry) {
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiry))
                .and()
                .signWith(getKey())
                .compact();
    }

    // ── Extract claims ─────────────────────────────────

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .clockSkewSeconds(60)   // ← allows 60s clock difference between machines
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ── Validate ───────────────────────────────────────

    /**
     * Used by JwtFilter — validates access tokens only
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername())
                    && !isTokenExpired(token)
                    && isAccessToken(token);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public boolean isAccessToken(String token) {
        try {
            return "access".equals(extractTokenType(token));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            return "refresh".equals(extractTokenType(token));
        } catch (Exception e) {
            return false;
        }
    }
}