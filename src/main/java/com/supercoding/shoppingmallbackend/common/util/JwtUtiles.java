package com.supercoding.shoppingmallbackend.common.util;

import com.supercoding.shoppingmallbackend.dto.ProfileDetail;
import com.supercoding.shoppingmallbackend.repository.ProfileRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtiles {
    @Value("${secret-key-source}")
    private String secretKey;
    private long tokenValidMillisecond = 1000L * 60 * 60 * 24 * 7; // 1주일
    private final ProfileRepository profileRepository;
    @PostConstruct
    public void setUp() {
        secretKey = Base64.getEncoder().encodeToString("super-coding".getBytes());
    }



    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("AUTHORIZATION");
    }

    public String createToken(Long user_idx, String role) {
        Claims claims = Jwts.claims()
                .setSubject(String.valueOf(user_idx));
        claims.put("role", role);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidMillisecond))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean validateToken(String jwtToken) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(jwtToken)
                    .getBody();
            Date now = new Date();
            return claims.getExpiration()
                    .after(now);
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication getAuthentication(String jwtToken) {
        ProfileDetail profile = ProfileDetail.from(profileRepository.loadUserByProfileIdx(getIdx(jwtToken)));
        return new UsernamePasswordAuthenticationToken(profile, "", profile.getAuthorities());
    }

    private Long getIdx(String jwtToken) {
        return Long.valueOf(Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwtToken)
                .getBody()
                .getSubject());
    }
}
