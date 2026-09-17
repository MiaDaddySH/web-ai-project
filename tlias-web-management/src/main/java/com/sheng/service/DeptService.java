package com.sheng.service;

import com.sheng.pojo.Dept;

import java.util.List;

public interface DeptService {
	List<Dept> findAll();

	void deleteDeptById(Integer id);

	void addDept(Dept dept);

	Dept findDeptById(Integer id);

	void updateDept(Dept dept);
}
