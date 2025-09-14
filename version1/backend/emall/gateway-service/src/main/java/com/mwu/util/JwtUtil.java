package com.mwu.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static javax.crypto.Cipher.SECRET_KEY;

@Component
public class JwtUtil {

    @Value("${jwt.secret-key}")
    private String secretKey;
    public Claims validateToken(String token) {
        return Jwts.parser().verifyWith((SecretKey) getSignKey()).build().parseSignedClaims(token).getPayload();
    }
//    public String generateToken(UserDetails userDetails) {
//
//        return Jwts.builder()
//                .subject(userDetails.getUsername())
//                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration(new Date(System.currentTimeMillis() +    TOKEN_VALIDITY))
//                .signWith(getSignInKey())
//                .compact();
//    }
    private Key getSignKey() {

        byte[] bytes = Base64.getDecoder()
                .decode(secretKey.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(bytes, "HmacSHA256");
//        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }


    public static boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new java.util.Date());
    }

    public static String getEmail(Claims claims) {
        return claims.getSubject();
    }
    public static String getRoles(Claims claims) {
        return claims.get("role", String.class);
    }
}

