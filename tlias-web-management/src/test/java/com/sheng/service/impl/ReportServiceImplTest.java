package com.sheng.service.impl;

import com.sheng.mapper.EmpMapper;
import com.sheng.mapper.StudentMapper;
import com.sheng.pojo.ClazzOption;
import com.sheng.pojo.JobOption;
import com.sheng.pojo.ValueOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * 报表业务层的数据转换测试。
 *
 * <p>Mapper 返回的是按行组织的 Map，而前端图表需要“标签列表 + 数值列表”。
 * 这类字段拆分很容易取错 key 或颠倒顺序，因此需要单独锁定。</p>
 */
@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private EmpMapper empMapper;

    @Mock
    private StudentMapper studentMapper;

    private ReportServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ReportServiceImpl(empMapper, studentMapper);
    }

    @Test
    void empJobDataShouldSplitLabelsAndValues() {
        // 使用两行数据才能同时验证字段映射和顺序保留。
        when(empMapper.countEmpJobData()).thenReturn(List.of(
                Map.of("job", "班主任", "num", 3),
                Map.of("job", "讲师", "num", 7)));

        JobOption result = service.empJobData();

        assertEquals(List.of("班主任", "讲师"), result.getJobList());
        assertEquals(List.of(3, 7), result.getDataList());
    }

    @Test
    void studentCountDataShouldSplitLabelsAndValues() {
        when(studentMapper.studentCountData()).thenReturn(List.of(
                Map.of("clazz", "Java 1", "num", 12),
                Map.of("clazz", "Java 2", "num", 9)));

        ClazzOption result = service.studentCountData();

        assertEquals(List.of("Java 1", "Java 2"), result.getClazzList());
        assertEquals(List.of(12, 9), result.getDataList());
    }

    @Test
    void optionReportsShouldDelegateToMappers() {
        // 这两个方法不做转换，测试重点是原样返回 Mapper 结果。
        List<ValueOption> genders = List.of(new ValueOption("男", 5));
        List<ValueOption> degrees = List.of(new ValueOption("本科", 8));
        when(empMapper.countEmpGenderData()).thenReturn(genders);
        when(studentMapper.studentDegreeData()).thenReturn(degrees);

        assertEquals(genders, service.empGenderData());
        assertEquals(degrees, service.studentDegreeData());
    }
}
