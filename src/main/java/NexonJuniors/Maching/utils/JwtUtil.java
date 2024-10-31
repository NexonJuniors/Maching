package NexonJuniors.Maching.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {
    private final static int ACCESS_TOKEN_TIME = 60 * 30;
    private final static int REFRESH_TOKEN_TIME = 60 * 60 * 24;
    private final static String REFRESH_TOKEN_ISSUER = "REF_MACHING";
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
    public String generateToken(Long id, int time){
        Claims claims = Jwts.claims()
                .setSubject(String.valueOf(id))
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(time)));

        if(time == REFRESH_TOKEN_TIME) claims.setIssuer(REFRESH_TOKEN_ISSUER);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(signingKey)
                .compact();
    }

    public boolean isValidate(String token){
        try{
            parseClaims(token);
            return true;
        }
        catch (Exception e){
            log.warn("유효하지 않은 토큰");
            return false;
        }
    }

    public boolean isExpired(String token){
        Claims claims = parseClaims(token);

        return claims.getExpiration().before(new Date());
    }

    public Claims parseClaims(String token){
        return jwtParser.parseClaimsJws(token).getBody();
    }
}
