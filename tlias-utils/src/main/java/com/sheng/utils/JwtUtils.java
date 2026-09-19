package com.sheng.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * JWT令牌工具类
 */
public final class JwtUtils {

	// Base64解码后为32字节，满足HS256密钥长度要求
	private static final String SECRET =
			"MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=";

	private static final SecretKey KEY = Keys.hmacShaKeyFor(
			Decoders.BASE64.decode(SECRET)
	);

	// 令牌有效期：12小时
	private static final long EXPIRATION_TIME =
			12 * 60 * 60 * 1000L;

	// 禁止创建工具类对象
	private JwtUtils() {
	}

	/**
	 * 生成JWT令牌
	 *
	 * @param claims 令牌中保存的数据
	 * @return JWT令牌
	 */
	public static String generateJwt(Map<String, Object> claims) {
		Date now = new Date();
		Date expiration = new Date(
				now.getTime() + EXPIRATION_TIME
		);

		return Jwts.builder()
		           .claims(claims)
		           .issuedAt(now)
		           .expiration(expiration)
		           .signWith(KEY)
		           .compact();
	}

	/**
	 * 解析JWT令牌
	 *
	 * @param jwt JWT令牌
	 * @return 令牌中的Claims数据
	 */
	public static Claims parseJwt(String jwt) {
		return Jwts.parser()
		           .verifyWith(KEY)
		           .build()
		           .parseSignedClaims(jwt)
		           .getPayload();
	}
}