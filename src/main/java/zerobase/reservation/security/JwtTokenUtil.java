package zerobase.reservation.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtTokenUtil {
    
    private final String TOKEN_PREFIX = "Bearer ";
    @Value("${spring.jwt.secret}")
    private String secretKey;
    
    public String getEmail(String token) {
        
        return parseClaims(token).getSubject();
    }
    
    public String getToken(String header) {
        
        return header.substring(TOKEN_PREFIX.length());
    }
    
    private SecretKey getSignKey(String secretKey) {
        
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    private Claims parseClaims(String token) {
        
        try {
            return Jwts.parser()
                    .verifyWith(getSignKey(secretKey))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
