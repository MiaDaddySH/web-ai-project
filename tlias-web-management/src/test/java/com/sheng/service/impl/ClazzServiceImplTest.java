package com.sheng.service.impl;

import com.sheng.mapper.ClazzMapper;
import com.sheng.mapper.StudentMapper;
import com.sheng.pojo.Clazz;
import com.sheng.pojo.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 班级业务层单元测试。
 *
 * <p>Mapper 全部使用 Mock，因为本组测试的目标是 Service 的分支、数据补全和调用顺序，
 * 而不是 SQL 是否正确。SQL 兼容性应在后续 MySQL 集成测试中验证。</p>
 */
@ExtendWith(MockitoExtension.class)
class ClazzServiceImplTest {

    @Mock
    private ClazzMapper clazzMapper;

    @Mock
    private StudentMapper studentMapper;

    private ClazzServiceImpl service;

    @BeforeEach
    void setUp() {
        // 手动注入 Mock，让被测对象只包含真实 Service 逻辑。
        service = new ClazzServiceImpl(clazzMapper, studentMapper);
    }

    @Test
    void addShouldSetTimestampsAndInsert() {
        Clazz clazz = new Clazz();

        service.add(clazz);

        assertNotNull(clazz.getCreateTime());
        assertEquals(clazz.getCreateTime(), clazz.getUpdateTime());
        verify(clazzMapper).insert(clazz);
    }

    @Test
    void updateShouldExecuteOnlyOnce() {
        Clazz clazz = new Clazz();
        clazz.setId(10);
        when(clazzMapper.update(clazz)).thenReturn(1);

        service.update(clazz);

        assertNotNull(clazz.getUpdateTime());
        // verify 默认要求恰好调用一次，用于防止更新 SQL 被重复执行。
        verify(clazzMapper).update(clazz);
    }

    @Test
    void updateShouldFailWhenClazzDoesNotExist() {
        Clazz clazz = new Clazz();
        when(clazzMapper.update(clazz)).thenReturn(0);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.update(clazz));

        assertEquals("班级不存在", exception.getMessage());
        verify(clazzMapper).update(clazz);
    }

    @Test
    void deleteShouldRejectClazzWithStudents() {
        when(studentMapper.findStudentsByClazzId(10)).thenReturn(List.of(new Student()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.delete(10));

        assertEquals("该班级下有学生，不能删除", exception.getMessage());
        // 只检查异常还不够，还要保证真正的删除方法从未被调用。
        verify(clazzMapper, never()).delete(10);
    }

    @Test
    void deleteShouldRemoveEmptyClazz() {
        when(studentMapper.findStudentsByClazzId(10)).thenReturn(List.of());

        service.delete(10);

        verify(clazzMapper).delete(10);
    }
}
