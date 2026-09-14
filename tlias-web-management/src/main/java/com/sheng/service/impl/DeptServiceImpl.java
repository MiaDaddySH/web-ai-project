package com.sheng.service.impl;

import com.sheng.mapper.DeptMapper;
import com.sheng.pojo.Dept;
import com.sheng.service.DeptService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeptServiceImpl implements DeptService {
	private final DeptMapper deptMapper;

	public DeptServiceImpl(DeptMapper deptMapper) {
		this.deptMapper = deptMapper;
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
		deptMapper.deleteDeptById(id);
	}

	@Override
	public void addDept(Dept dept) {
		dept.setCreateTime(java.time.LocalDateTime.now());
		dept.setUpdateTime(java.time.LocalDateTime.now());
		deptMapper.addDept(dept);
	}
}
