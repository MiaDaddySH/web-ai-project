package com.sheng;

import com.sheng.utils.JwtUtils;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JWT 工具类的单元测试。
 *
 * <p>这里直接测试生产代码 {@link JwtUtils}，而不在测试中重新实现一套 JWT 逻辑。
 * 否则即使生产代码写错，复制出来的测试代码仍可能自己通过。</p>
 */
class JwtTest {

	@Test
	void shouldGenerateAndParseJwt() {
		// 生成后再解析，同时验证业务字段和标准时间字段都被保留。
		String jwt = JwtUtils.generateJwt(Map.of("id", 1, "username", "admin"));
		var claims = JwtUtils.parseJwt(jwt);

		assertNotNull(jwt);
		assertEquals("admin", claims.get("username", String.class));
		assertEquals(1, claims.get("id", Integer.class));
		assertNotNull(claims.getIssuedAt());
		assertNotNull(claims.getExpiration());
	}

	@Test
	void shouldRejectTamperedJwt() {
		String jwt = JwtUtils.generateJwt(Map.of("id", 1));
		// 只改动签名的最后一个字符，模拟令牌在传输中被篡改。
		String tampered = jwt.substring(0, jwt.length() - 1)
				+ (jwt.endsWith("a") ? "b" : "a");

		// 安全要求：篡改后的令牌必须解析失败，不能被当作合法用户。
		assertThrows(JwtException.class, () -> JwtUtils.parseJwt(tampered));
	}
}
