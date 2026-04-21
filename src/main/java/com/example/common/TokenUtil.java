package com.example.common;

import cn.hutool.core.util.IdUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenUtil {

    public final static String secret = "5b177ee2f9c9403ab23a51fcaa010c40";

    public final static Long expiration = 3600000L;

    public final static String header = "jwtHeader";

    public static String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }



    /**
     * 从令牌中获取用户名
     * @param token 令牌
     * @return 用户名
     */
    public static String getUsernameFromToken(String token){
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = String.valueOf(claims.get("username"));
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 从令牌中获取数据声明
     * @param token
     * @return
     */
    private static Claims getClaimsFromToken(String token){
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    /**
     * 判断令牌是否过期
     * @param token 令牌
     * @return 是否过期
     */
    public static boolean isTokenExpired(String token){
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证令牌
     * @param token 令牌
     * @param username 用户
     * @return 是否有效
     */
    public static boolean validateToken(String token, String username){
        return StringUtil.username.equals(username) && !isTokenExpired(token);
    }
}
