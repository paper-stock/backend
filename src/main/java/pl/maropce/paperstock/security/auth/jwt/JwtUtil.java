package pl.maropce.paperstock.security.auth.jwt;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.maropce.paperstock.security.AppUserDetails;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret}")
    private String JWT_SECRET;

    @Value("${jwt.expiration}")
    private long EXPIRATION_TIME;

    private SecretKey SECRET_KEY;

    @PostConstruct
    public void init() {
        SECRET_KEY = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    }

    public String generateToken(AppUserDetails appUserDetails) {
        return Jwts.builder()
                .subject(appUserDetails.getUsername())
                .claim("roles", appUserDetails.getAuthorities())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String extractUsername(String token) {
        JwtParser jwtParser = Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build();

        return jwtParser.parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
    private Date extractExpiration(String token) {
        JwtParser jwtParser = Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build();

        return jwtParser.parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, AppUserDetails appUserDetails) {
        final String username = extractUsername(token);
        return username.equals(appUserDetails.getUsername()) && !isTokenExpired(token);
    }
}
