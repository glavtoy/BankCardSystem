package ru.glavtoy.bankcardsystem.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JWTTokenUtil {

    @Value("${JWT_SECRET:${jwt.secret}}")
    private String secret;

    @Value("${jwt.lifetime:PT1H}")
    private Duration jwtLifetime;

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put("roles", roles);

        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtLifetime.toMillis());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT ключ превышает 256 бит");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameByToken(String token) {
        return getClaimsByToken(token).getSubject();
    }

    public List<String> getRolesByToken(String token) {
        Claims claims = getClaimsByToken(token);
        List<?> rolesRaw = claims.get("roles", List.class);
        if (rolesRaw == null) return Collections.emptyList();
        List<String> roles = new ArrayList<>();
        for (Object role : rolesRaw) {
            if (!(role instanceof String)) throw new IllegalStateException("Невалидные роли токена");
            roles.add((String) role);
        }
        return roles;
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            Claims claims = getClaimsByToken(token);
            return claims.getSubject().equals(userDetails.getUsername())
                    && claims.getExpiration().after(new Date());
        } catch (JwtException e) {
            return false;
        }
    }

    private Claims getClaimsByToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}