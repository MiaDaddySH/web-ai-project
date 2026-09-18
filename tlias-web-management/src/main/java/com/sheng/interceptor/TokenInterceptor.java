package com.sheng.interceptor;

import com.sheng.utils.JwtUtils;
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
		//获取请求路径
		String path = request.getRequestURI();

		//获取请求头中的token
		String token = request.getHeader("token");

		//判断token是否存在，如果不在的话，那么就返回401错误码
		if (token == null || token.isEmpty()) {
			log.info("token不存在，响应401");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}

		//判断token是否仍然有效，如果无效则返回401错误
		try {
			JwtUtils.parseJwt(token);
		} catch (Exception e) {
			log.info("token无效，响应401");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}
		//放行
		log.info("token有效，放行");
		return true;
	}
}
