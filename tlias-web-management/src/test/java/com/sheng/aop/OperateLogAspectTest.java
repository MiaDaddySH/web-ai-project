package com.sheng.aop;

import com.sheng.mapper.OperateLogMapper;
import com.sheng.pojo.OperateLog;
import com.sheng.utils.CurrentHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 操作日志切面的单元测试。
 *
 * <p>切面的核心责任是“不改变原业务结果，同时准确记录操作”。
 * 因此这里分别覆盖正常返回、返回 null、业务异常和日志入库失败四种情况。</p>
 */
@ExtendWith(MockitoExtension.class)
class OperateLogAspectTest {

    // Mapper 使用 Mock，让测试只关注切面逻辑，不连接真实数据库。
    @Mock
    private OperateLogMapper operateLogMapper;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private Signature signature;

    private OperateLogAspect aspect;

    @BeforeEach
    void setUp() {
        aspect = new OperateLogAspect(operateLogMapper);
        // 伪造 AOP 调用现场：目标对象、方法名和实参都会被写入日志。
        when(joinPoint.getTarget()).thenReturn(this);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("update");
        when(joinPoint.getArgs()).thenReturn(new Object[]{3, "name"});
        CurrentHolder.setCurrentId(15);
    }

    @AfterEach
    void cleanThreadLocal() {
        // ThreadLocal 会被测试线程复用，必须清理，否则其他测试可能读到残留的用户 ID。
        CurrentHolder.remove();
    }

    @Test
    void shouldRecordSuccessfulOperation() throws Throwable {
        Object expected = new Object();
        when(joinPoint.proceed()).thenReturn(expected);

        Object result = aspect.recordOperateLog(joinPoint);

        assertSame(expected, result);
        // ArgumentCaptor 用来取出真正传给 Mapper 的日志对象，便于逐字段校验。
        ArgumentCaptor<OperateLog> captor = ArgumentCaptor.forClass(OperateLog.class);
        verify(operateLogMapper).insert(captor.capture());
        OperateLog log = captor.getValue();
        assertEquals(15, log.getOperateEmpId());
        assertEquals("update", log.getMethodName());
        assertEquals("[3, name]", log.getMethodParams());
        assertEquals(expected.toString(), log.getReturnValue());
        assertNotNull(log.getOperateTime());
        assertTrue(log.getCostTime() >= 0);
    }

    @Test
    void shouldRecordNullReturnValue() throws Throwable {
        // void 方法在 AOP 中表现为 null，日志不能因为调用 toString() 而抛异常。
        when(joinPoint.proceed()).thenReturn(null);

        aspect.recordOperateLog(joinPoint);

        ArgumentCaptor<OperateLog> captor = ArgumentCaptor.forClass(OperateLog.class);
        verify(operateLogMapper).insert(captor.capture());
        assertEquals("null", captor.getValue().getReturnValue());
    }

    @Test
    void shouldRecordAndRethrowBusinessException() throws Throwable {
        IllegalStateException failure = new IllegalStateException("business failed");
        when(joinPoint.proceed()).thenThrow(failure);

        // 日志可以记录异常，但必须把原始业务异常继续抛给上层。
        IllegalStateException thrown = assertThrows(
                IllegalStateException.class, () -> aspect.recordOperateLog(joinPoint));

        assertSame(failure, thrown);
        ArgumentCaptor<OperateLog> captor = ArgumentCaptor.forClass(OperateLog.class);
        verify(operateLogMapper).insert(captor.capture());
        assertEquals("执行异常：business failed", captor.getValue().getReturnValue());
    }

    @Test
    void loggingFailureShouldNotChangeBusinessResult() throws Throwable {
        when(joinPoint.proceed()).thenReturn("ok");
        doThrow(new RuntimeException("logging failed")).when(operateLogMapper).insert(org.mockito.ArgumentMatchers.any());

        // 日志是辅助功能，日志表写入失败不应让已成功的业务请求变成失败。
        assertEquals("ok", aspect.recordOperateLog(joinPoint));
    }
}
