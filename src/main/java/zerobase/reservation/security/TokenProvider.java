package zerobase.reservation.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import zerobase.reservation.exception.CustomException;
import zerobase.reservation.exception.ErrorCode;
import zerobase.reservation.service.CustomerService;
import zerobase.reservation.service.ManagerService;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenProvider {
    
    private static final String KEY_ROLES = "roles";
    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1 hour
    private final CustomerService customerService;
    private final ManagerService managerService;
    @Value("${spring.jwt.secret}")
    private String secretKey;
    
    public String generateToken(String email, List<String> roles) {
        
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);
        
        return Jwts.builder()
                .claim(KEY_ROLES, roles)
                .subject(email)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSignKey(secretKey), Jwts.SIG.HS512)
                .compact();
    }
    
    public Authentication getAuthentication(String jwt) {
        
        String role = getRole(jwt).getFirst();
        UserDetails userDetails = null;
        
        switch (role) {
            case "ROLE_CUSTOMER" -> {
                userDetails = customerService.loadUserByUsername(getEmail(jwt));
            }
            
            case "ROLE_MANAGER" -> {
                userDetails = managerService.loadUserByUsername(getEmail(jwt));
            }
        }
        
        if (userDetails == null) {
            throw new CustomException(ErrorCode.NO_SUCH_USER);
        }
        
        return new UsernamePasswordAuthenticationToken(
                userDetails, userDetails.getPassword(),
                userDetails.getAuthorities());
    }
    
    public String getEmail(String token) {
        
        return parseClaims(token).getSubject();
    }
    
    public List<String> getRole(String token) {
        
        return (List<String>) parseClaims(token).get(KEY_ROLES);
    }
    
    public boolean validateToken(String token) {
        
        if (!StringUtils.hasText(token)) return false;
        
        Claims claims = parseClaims(token);
        return !claims.getExpiration().before(new Date());
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