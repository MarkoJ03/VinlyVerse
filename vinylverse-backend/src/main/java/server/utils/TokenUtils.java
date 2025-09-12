package server.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

@Component
public class TokenUtils {

    private final Key key;
    private final long expirationMs;

    public TokenUtils(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms:3600000}") long expirationMs // podrazumevano 1h
    ) {
        // HMAC-SHA ključ; secret mora imati ≥32 bajta
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)        // novi JJWT stil verifikacije
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Claims getClaims(String token) {
        try {
            return parseClaims(token);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isExpired(String token) {
        Claims c = getClaims(token);
        return c == null || c.getExpiration() == null || c.getExpiration().before(new Date());
    }

    public boolean validateToken(String token) {
        return getClaims(token) != null && !isExpired(token);
    }

    public String getUsername(String token) {
        Claims c = getClaims(token);
        return c == null ? null : c.getSubject();
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("authorities", userDetails.getAuthorities());

        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .claims(payload)
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }
}
