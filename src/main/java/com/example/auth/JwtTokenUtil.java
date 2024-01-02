package com.example.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtTokenUtil {

    //  JWT 토큰 발급
    public static String createToken(String loginId, String key, long expireTimeMs) {
        //  Claim = JWT 토큰에 들어갈 정보
        //  Claim에 loginId를 넣어줌으로써 나중에 loginId를 꺼낼 수 있음.
        Claims claims = Jwts.claims();
        claims.put("loginId", loginId);

        return Jwts.builder()
                        .setClaims(claims)
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs))
                        .signWith(SignatureAlgorithm.HS256, key)
                        .compact();
    }

    //  Claims 에서 loginId 꺼내기
    public static String getLoginId(String token, String secretKey) {
        return extractClaims(token, secretKey).get("loginId").toString();
    }

    //  발급된 Token이 만료 시간이 지났는지 체크
    public static boolean isExpired(String token, String secretKey) {
        Date expiredDate = extractClaims(token, secretKey).getExpiration();
        //  Token의 만료 날짜가 지금보다 이전인지 check
        return expiredDate.before(new Date());
    }

    // SecretKey를 사용해 Token Parsing
    private static Claims extractClaims(String token, String secretKey) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }
}
