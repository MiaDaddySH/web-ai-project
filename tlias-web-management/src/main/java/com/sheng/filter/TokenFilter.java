package com.sheng.filter;

import com.sheng.utils.JwtUtils;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@WebFilter(urlPatterns = "/*")
@Slf4j
public class TokenFilter implements Filter {
	@Override
	public void doFilter(
			ServletRequest request, ServletResponse response,
			FilterChain chain
	) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		//获取请求路径
		String path = httpRequest.getRequestURI();


		//判断是否是登录请求，如果路径中包含/login，说明是登录操作，放行
		if (path.contains("/login")) {
			chain.doFilter(request, response);
			return;
		}
		//获取请求头中的token
		String token = httpRequest.getHeader("token");

		//判断token是否存在，如果不在的话，那么就返回401错误码
		if (token == null || token.isEmpty()) {
			log.info("token不存在，响应401");
			httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		//判断token是否仍然有效，如果无效则返回401错误
		try {
			JwtUtils.parseJwt(token);
		} catch (Exception e) {
			log.info("token无效，响应401");
			httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		//放行
		log.info("token有效，放行");
		chain.doFilter(request, response);
	}
}
