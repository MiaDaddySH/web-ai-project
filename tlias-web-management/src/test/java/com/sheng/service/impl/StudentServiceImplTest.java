package com.sheng.service.impl;

import com.sheng.mapper.StudentMapper;
import com.sheng.pojo.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 学生业务层单元测试。
 *
 * <p>除常规增删改外，重点覆盖违纪次数和扣分的累加规则。</p>
 */
@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private StudentMapper studentMapper;

    private StudentServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new StudentServiceImpl(studentMapper);
    }

    @Test
    void addShouldSetTimestampsAndInsert() {
        Student student = new Student();

        service.add(student);

        assertNotNull(student.getCreateTime());
        // 创建时尚未发生修改，两个时间应使用同一时刻。
        assertEquals(student.getCreateTime(), student.getUpdateTime());
        verify(studentMapper).insert(student);
    }

    @Test
    void updateShouldSetTimestamp() {
        Student student = new Student();

        service.update(student);

        assertNotNull(student.getUpdateTime());
        verify(studentMapper).update(student);
    }

    @Test
    void updateViolationScoreShouldIncrementCountAndScore() {
        Student student = new Student();
        student.setId(5);
        student.setViolationCount((short) 2);
        student.setViolationScore((short) 6);
        when(studentMapper.findById(5)).thenReturn(student);

        service.updateViolationScore(5, 3);

        // 每次处罚既增加一次违纪记录，也在原有分数上累加本次扣分。
        assertEquals((short) 3, student.getViolationCount());
        assertEquals((short) 9, student.getViolationScore());
        assertNotNull(student.getUpdateTime());
        verify(studentMapper).updateViolationScore(student);
    }

    @Test
    void queriesAndDeleteShouldDelegateToMapper() {
        // 无额外规则的薄 Service 方法，只验证参数和返回值没有被改写。
        Student student = new Student();
        when(studentMapper.findById(5)).thenReturn(student);

        assertEquals(student, service.findById(5));
        service.deleteByBatch(List.of(5, 6));

        verify(studentMapper).deleteByBatch(List.of(5, 6));
    }
}
