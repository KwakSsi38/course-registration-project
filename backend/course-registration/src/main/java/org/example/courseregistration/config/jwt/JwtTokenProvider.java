package org.example.courseregistration.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {
    private final String SECRET_KEY_STRING = "my-very-secret-key-must-be-at-least-256-bits-long!";
    private final SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));

    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 2; // 2시간

    public String createToken(String studentId, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(studentId);
        claims.put("roles", roles);

        Date now = new Date();
        Date validity = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey)  // SecretKey 객체 넣기
                .compact();
    }

    public String getStudentId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey) // SecretKey는 Key 객체여야 함
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject(); // 또는 studentId 직접 claims에서 가져오기
    }

    public boolean hasRole(String token, String role) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        List<String> roles = (List<String>) claims.get("roles");
        return roles != null && roles.contains(role);
    }



}

