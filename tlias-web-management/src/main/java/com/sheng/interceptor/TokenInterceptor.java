package com.sheng.interceptor;

import com.sheng.utils.CurrentHolder;
import com.sheng.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler
	) throws Exception {
		//获取请求头中的token
		String token = request.getHeader("token");

		//判断token是否存在，如果不在的话，那么就返回401错误码
		if (token == null || token.isEmpty()) {
			log.info("token不存在，响应401");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}

		//判断token是否仍然有效，如果无效则返回401错误
		try {          // 解析 JWT
			Claims claims = JwtUtils.parseJwt(token);

			// 获取生成 JWT 时保存的员工 ID
			Object idValue = claims.get("id");

			if (!(idValue instanceof Number number)) {
				log.info("token中不存在有效的员工ID，响应401");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return false;
			}

			Integer employeeId = number.intValue();

			// 保存到当前线程
			CurrentHolder.setCurrentId(employeeId);

			log.info("token有效，当前员工ID：{}", employeeId);
		} catch (Exception e) {
			log.info("token无效，响应401");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}
		//放行
		log.info("token有效，放行");
		return true;
	}

	@Override
	public void afterCompletion(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler,
			Exception ex
	) {
		// 请求结束后清理，防止线程复用导致用户信息混乱
		CurrentHolder.remove();
	}
}
