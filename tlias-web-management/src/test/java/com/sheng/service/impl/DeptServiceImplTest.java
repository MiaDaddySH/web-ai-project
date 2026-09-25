package com.sheng.service.impl;

import com.sheng.mapper.DeptMapper;
import com.sheng.mapper.EmpMapper;
import com.sheng.pojo.Dept;
import com.sheng.pojo.Emp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 部门业务层单元测试。
 *
 * <p>重点验证时间字段是否由 Service 统一补全，以及部门下有员工时的删除保护。</p>
 */
@ExtendWith(MockitoExtension.class)
class DeptServiceImplTest {

	@Mock
	private DeptMapper deptMapper;

	@Mock
	private EmpMapper empMapper;

	private DeptServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new DeptServiceImpl(deptMapper, empMapper);
	}

	@Test
	void addShouldSetTimestampsAndInsert() {
		Dept dept = new Dept();
		service.addDept(dept);

		assertNotNull(dept.getCreateTime());
		// 同一次新增操作应共用同一个 now，避免创建和更新时间出现微小偏差。
		assertEquals(dept.getCreateTime(), dept.getUpdateTime());
		verify(deptMapper).addDept(dept);
	}

	@Test
	void updateShouldSetTimestamp() {
		Dept dept = new Dept();
		service.updateDept(dept);

		assertNotNull(dept.getUpdateTime());
		verify(deptMapper).updateDept(dept);
	}

	@Test
	void deleteShouldRejectDepartmentWithEmployees() {
		when(empMapper.findEmpByDeptId(3)).thenReturn(List.of(new Emp()));

		RuntimeException exception = assertThrows(RuntimeException.class, () -> service.deleteDeptById(3));

		assertEquals("该部门下有员工，不能删除", exception.getMessage());
		// 保护分支中不允许误调删除 SQL。
		verify(deptMapper, never()).deleteDeptById(3);
	}

	@Test
	void deleteShouldRemoveDepartmentWithoutEmployees() {
		when(empMapper.findEmpByDeptId(3)).thenReturn(List.of());

		service.deleteDeptById(3);

		verify(deptMapper).deleteDeptById(3);
	}

	@Test
	void findAllShouldReturnMapperResult() {
		List<Dept> expected = List.of(new Dept());
		when(deptMapper.findAll()).thenReturn(expected);

		List<Dept> actual = service.findAll();

		assertSame(expected, actual);
	}

	@Test
	void findDeptByIdShouldReturnMapperResult() {
		Dept expected = new Dept();
		when(deptMapper.findDeptById(3)).thenReturn(expected);

		Dept actual = service.findDeptById(3);

		assertSame(expected, actual);
	}
}
