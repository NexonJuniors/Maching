package NexonJuniors.Maching.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {
    private final Key signingKey;
    private final JwtParser jwtParser;

    public JwtUtil(@Value("${jwt.secret}") String jwtSecret){
        signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        jwtParser = Jwts
                .parserBuilder()
                .setSigningKey(signingKey)
                .build();
    }

    // JWT 만드는 메소드
    public String generateToken(Long id){
        Claims claims = Jwts.claims()
                .setSubject(String.valueOf(id))
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(600)));

        return Jwts.builder()
                .setClaims(claims)
                .signWith(signingKey)
                .compact();
    }
}
