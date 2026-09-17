package com.sheng.service.impl;

import com.sheng.mapper.DeptMapper;
import com.sheng.mapper.EmpMapper;
import com.sheng.pojo.Dept;
import com.sheng.service.DeptService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeptServiceImpl implements DeptService {
	private final DeptMapper deptMapper;
	private final EmpMapper empMapper;

	public DeptServiceImpl(DeptMapper deptMapper, EmpMapper empMapper) {

		this.deptMapper = deptMapper;
		this.empMapper = empMapper;
	}

	@Override
	public List<Dept> findAll() {
		return deptMapper.findAll();
	}

	@Override
	public Dept findDeptById(Integer id) {
		return deptMapper.findDeptById(id);
	}

	@Override
	public void updateDept(Dept dept) {
		dept.setUpdateTime(java.time.LocalDateTime.now());
		deptMapper.updateDept(dept);
	}

	@Override
	public void deleteDeptById(Integer id) {
		if (!empMapper.findEmpByDeptId(id).isEmpty()) {
			throw new RuntimeException("该部门下有员工，不能删除");
		} else {
			deptMapper.deleteDeptById(id);
		}
	}

	@Override
	public void addDept(Dept dept) {
		dept.setCreateTime(java.time.LocalDateTime.now());
		dept.setUpdateTime(java.time.LocalDateTime.now());
		deptMapper.addDept(dept);
	}
}
