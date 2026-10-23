package pl.maropce.paperstock.jwt.internal;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.maropce.paperstock.security.AppUserDetails;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long expirationTime;

    private SecretKey secretKey;
    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        jwtParser = Jwts.parser()
                .verifyWith(secretKey)
                .build();
    }

    public String generateToken(AppUserDetails appUserDetails) {
        return Jwts.builder()
                .subject(appUserDetails.getUsername())
                .claim("roles", appUserDetails.getAuthorities())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return jwtParser.parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token, AppUserDetails appUserDetails) {
        final String username = extractUsername(token);
        return username.equals(appUserDetails.getUsername());
    }
}
