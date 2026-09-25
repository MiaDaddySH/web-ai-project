package com.sheng;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Spring Boot 应用上下文冒烟测试。
 *
 * <p>这不是纯单元测试：它会启动完整 Spring 容器，用于尽早发现 Bean 注入、
 * 配置绑定和 MyBatis 初始化问题。测试使用 test Profile 的 H2，不连接开发环境 MySQL。</p>
 */
@SpringBootTest
@ActiveProfiles("test")
class TliasWebManagementApplicationTests {

	@Test
	void contextLoads() {
		// 方法体留空即可：只要 Spring 容器启动成功，这个测试就达到目的。
	}

}
