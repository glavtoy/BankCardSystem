package ru.glavtoy.bankcardsystem.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JWTTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.lifetime}")
    private Duration jwtLifetime;

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        claims.put("roles", roles);

        Date starting = new Date();
        Date ending = new Date(starting.getTime() + jwtLifetime.toMillis());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(starting)
                .setExpiration(ending)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String getUsernameByToken(String token) {
        return getClaimsByToken(token).getSubject();
    }

    public List<String> getRolesByToken(String token) {
        Claims claims = getClaimsByToken(token);
        if (claims.containsKey("roles")) {
            return (List<String>) claims.get("roles");
        }
        return Collections.emptyList();
    }

    public boolean isTokenExpired(String token) {
        Date expiration = getClaimsByToken(token).getExpiration();
        return expiration.before(new Date());
    }

    private Claims getClaimsByToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new RuntimeException("Невалидный JWT токен: " + e.getMessage());
        }
    }
}