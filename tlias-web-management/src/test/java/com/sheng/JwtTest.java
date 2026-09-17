package com.sheng;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JwtTest {

	// 仅用于测试，Base64解码后为32字节
	private static final String SECRET =
			"MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=";

	private static final SecretKey KEY = Keys.hmacShaKeyFor(
			Decoders.BASE64.decode(SECRET)
	);

	/**
	 * 生成JWT
	 */
	private String generateJwt() {
		Map<String, Object> claims = new HashMap<>();
		claims.put("id", 1);
		claims.put("username", "admin");

		Date now = new Date();
		Date expiration = new Date(
				now.getTime() + 24 * 60 * 60 * 1000L
		);

		return Jwts.builder()
		           .claims(claims)
		           .issuedAt(now)
		           .expiration(expiration)
		           .signWith(KEY)
		           .compact();
	}

	@Test
	public void testGenerateJwt() {
		String jwt = generateJwt();

		System.out.println(jwt);
		assertNotNull(jwt);
	}

	@Test
	public void testParseJwt() {
		// 使用相同密钥生成测试令牌
		String jwt = generateJwt();

		Claims claims = Jwts.parser()
		                    .verifyWith(KEY)
		                    .build()
		                    .parseSignedClaims(jwt)
		                    .getPayload();

		System.out.println(claims);

		assertEquals("admin", claims.get("username", String.class));
		assertEquals(1, claims.get("id", Integer.class));
		assertNotNull(claims.getIssuedAt());
		assertNotNull(claims.getExpiration());
	}
}