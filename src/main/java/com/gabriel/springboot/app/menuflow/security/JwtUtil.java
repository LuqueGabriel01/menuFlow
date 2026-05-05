package com.gabriel.springboot.app.menuflow.security;

import static com.gabriel.springboot.app.menuflow.constants.SecurityConstants.AUTHORITIES_KEY;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration}")
    private Long expiration;

    private SecretKey secretKey;

    @PostConstruct
    protected void init() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, Collection<? extends GrantedAuthority> authorities) {

        Map<String,Object> claims = new HashMap<>();

        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        claims.put(AUTHORITIES_KEY, roles);

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try{
            getClaimsFromToken(token);
            return true;
        }catch (IllegalArgumentException e){
            log.error("The JWT token is not supported: {}", e.getMessage());
        }catch (ExpiredJwtException e){
            log.error("Token expired: {}", e.getMessage());
        }catch (UnsupportedJwtException e){
            log.error("Token not supported: {}", e.getMessage());
        }catch (MalformedJwtException e){
            log.error("Malformed token: {}", e.getMessage());
        }catch (SignatureException e){
            log.error("Invalid signature: {}", e.getMessage());
        }catch (JwtException e){
            log.error("Error validating token: {}", e.getMessage());
        }catch (Exception e){
            log.error("Unexpected error: {}", e.getMessage());
        }

        return false;
    }

    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public Collection<GrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Object authorities = claims.get(AUTHORITIES_KEY);

        if (authorities instanceof List<?>){
            return ((List<?>) authorities).stream()
                    .map(role -> new SimpleGrantedAuthority(String.valueOf(role)))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private Claims getClaimsFromToken(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
