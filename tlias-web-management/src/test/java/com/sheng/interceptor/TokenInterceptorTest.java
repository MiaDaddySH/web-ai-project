package com.sheng.interceptor;

import com.sheng.utils.CurrentHolder;
import com.sheng.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 身份验证拦截器的单元测试。
 *
 * <p>测试不启动 Web 服务器，而是用 Mock 的 request/response 直接调用拦截器，
 * 这样能快速验证鉴权分支和 HTTP 401 响应。</p>
 */
@ExtendWith(MockitoExtension.class)
class TokenInterceptorTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private final TokenInterceptor interceptor = new TokenInterceptor();

    @AfterEach
    void cleanThreadLocal() {
        // 无论用例成功还是失败都主动清理，保证测试之间相互独立。
        CurrentHolder.remove();
    }

    @Test
    void shouldRejectMissingToken() throws Exception {
        when(request.getHeader("token")).thenReturn(null);

        assertFalse(interceptor.preHandle(request, response, new Object()));
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertNull(CurrentHolder.getCurrentId());
    }

    @Test
    void shouldRejectInvalidToken() throws Exception {
        when(request.getHeader("token")).thenReturn("invalid-token");

        assertFalse(interceptor.preHandle(request, response, new Object()));
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void shouldRejectTokenWithoutEmployeeId() throws Exception {
        // 签名合法不等于业务上合法：缺少员工 ID 时仍不能放行。
        String token = JwtUtils.generateJwt(Map.of("username", "alice"));
        when(request.getHeader("token")).thenReturn(token);

        assertFalse(interceptor.preHandle(request, response, new Object()));
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void shouldStoreEmployeeIdAndCleanItAfterRequest() throws Exception {
        String token = JwtUtils.generateJwt(Map.of("id", 12, "username", "alice"));
        when(request.getHeader("token")).thenReturn(token);

        assertTrue(interceptor.preHandle(request, response, new Object()));
        assertEquals(12, CurrentHolder.getCurrentId());

        // 请求完成后必须删除 ThreadLocal，防止线程池复用时发生用户串号。
        interceptor.afterCompletion(request, response, new Object(), null);
        assertNull(CurrentHolder.getCurrentId());
    }
}
