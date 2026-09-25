package com.sheng.service.impl;

import com.sheng.mapper.EmpExprMapper;
import com.sheng.mapper.EmpMapper;
import com.sheng.pojo.Emp;
import com.sheng.pojo.EmpExpr;
import com.sheng.pojo.EmpLog;
import com.sheng.pojo.LoginResponse;
import com.sheng.service.EmpLogService;
import com.sheng.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 员工业务层单元测试。
 *
 * <p>员工操作涉及主表、工作经历子表和操作日志，是最需要验证调用顺序与数据传递的 Service。
 * 本测试使用 Mockito 隔离所有 Mapper，不依赖 MySQL。</p>
 */
@ExtendWith(MockitoExtension.class)
class EmpServiceImplTest {

    @Mock
    private EmpMapper empMapper;

    @Mock
    private EmpExprMapper empExprMapper;

    @Mock
    private EmpLogService empLogService;

    private EmpServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new EmpServiceImpl(empMapper, empExprMapper, empLogService);
    }

    @Test
    void addShouldInsertEmployeeExperiencesAndLog() {
        Emp emp = new Emp();
        emp.setUsername("alice");
        EmpExpr first = new EmpExpr();
        EmpExpr second = new EmpExpr();
        emp.setExprList(List.of(first, second));
        // MyBatis 插入后会回填自增主键；Mock 不会自动做这件事，所以用 doAnswer 模拟回填 ID。
        doAnswer(invocation -> {
            ((Emp) invocation.getArgument(0)).setId(7);
            return null;
        }).when(empMapper).insert(emp);

        service.add(emp);

        assertNotNull(emp.getCreateTime());
        assertEquals(emp.getCreateTime(), emp.getUpdateTime());
        assertEquals(7, first.getEmpId());
        assertEquals(7, second.getEmpId());
        verify(empExprMapper).insertBatch(emp.getExprList());

        // 捕获日志实参，确认 finally 中生成的内容完整。
        ArgumentCaptor<EmpLog> logCaptor = ArgumentCaptor.forClass(EmpLog.class);
        verify(empLogService).insertLog(logCaptor.capture());
        assertNotNull(logCaptor.getValue().getOperateTime());
        assertTrue(logCaptor.getValue().getInfo().contains("添加员工信息"));
    }

    @Test
    void addWithoutExperiencesShouldStillWriteLog() {
        Emp emp = new Emp();

        service.add(emp);

        // 空工作经历不应产生空批量 SQL，但员工新增日志仍需保留。
        verify(empExprMapper, never()).insertBatch(any());
        verify(empLogService).insertLog(any(EmpLog.class));
    }

    @Test
    void addShouldWriteLogEvenWhenInsertFails() {
        Emp emp = new Emp();
        doThrow(new RuntimeException("insert failed")).when(empMapper).insert(emp);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.add(emp));

        assertEquals("insert failed", exception.getMessage());
        // 生产代码在 finally 中记录尝试结果，所以主表插入失败时也应写日志。
        verify(empLogService).insertLog(any(EmpLog.class));
    }

    @Test
    void updateShouldReplaceEmployeeExperiences() {
        Emp emp = new Emp();
        emp.setId(8);
        EmpExpr experience = new EmpExpr();
        emp.setExprList(List.of(experience));

        service.update(emp);

        assertNotNull(emp.getUpdateTime());
        assertEquals(8, experience.getEmpId());
        verify(empMapper).updateEmp(emp);
        // 当前实现采用“先删旧经历，再插新经历”，两个调用缺一不可。
        verify(empExprMapper).deleteByEmpIds(List.of(8));
        verify(empExprMapper).insertBatch(emp.getExprList());
    }

    @Test
    void deleteShouldRemoveEmployeeAndExperiences() {
        List<Integer> ids = List.of(2, 4);

        service.delete(ids);

        verify(empMapper).deleteByIds(ids);
        verify(empExprMapper).deleteByEmpIds(ids);
    }

    @Test
    void loginShouldAttachValidJwt() {
        Emp credentials = new Emp();
        LoginResponse response = new LoginResponse(9, "alice", "Alice", null);
        when(empMapper.login(credentials)).thenReturn(response);

        LoginResponse result = service.login(credentials);

        assertNotNull(result.getToken());
        // 不只检查 token 字符串非空，还要真正解析，确认用户身份已写入 claims。
        assertEquals(9, JwtUtils.parseJwt(result.getToken()).get("id", Integer.class));
        assertEquals("alice", JwtUtils.parseJwt(result.getToken()).get("username", String.class));
    }

    @Test
    void loginShouldReturnNullForInvalidCredentials() {
        Emp credentials = new Emp();
        when(empMapper.login(credentials)).thenReturn(null);

        assertNull(service.login(credentials));
    }
}
