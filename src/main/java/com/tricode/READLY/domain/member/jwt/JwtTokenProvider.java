package com.tricode.READLY.domain.member.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final long accessTokenValidityInMilliseconds;

    // application.yml에 jwt.secret 값을 32바이트 이상으로 설정해주세요.
    public JwtTokenProvider(@Value("${jwt.secret:defaultSecretKeyVeryVeryLongAndSecureEnough12345!}") String secretKey,
                            @Value("${jwt.access-token-validity-in-seconds:3600}") long accessTokenValidityInSeconds) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenValidityInMilliseconds = accessTokenValidityInSeconds * 1000;
    }

    // 토큰 생성
    public String createToken(String email, Long memberId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + this.accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(email)
                .claim("memberId", memberId)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰에서 memberId 추출
    public Long getMemberId(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().get("memberId", Long.class);
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false; // 유효하지 않은 토큰
        }
    }
}
