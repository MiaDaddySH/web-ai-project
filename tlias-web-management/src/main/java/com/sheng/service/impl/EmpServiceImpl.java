package com.sheng.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sheng.mapper.EmpMapper;
import com.sheng.pojo.Emp;
import com.sheng.pojo.PageResult;
import com.sheng.service.EmpService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpServiceImpl implements EmpService {
	private final EmpMapper empMapper;
	public EmpServiceImpl(EmpMapper empMapper) {
		this.empMapper = empMapper;
	}

	/**
	 * 分页查询
	 * @param page 当前页
	 * @param pageSize 每页大小
	 * @return 分页结果
	 */
	@Override
	public PageResult<Emp> page(Integer page, Integer pageSize) {
		//1. 设置分页参数
		PageHelper.startPage(page, pageSize);

		//2. 执行查询
		List<Emp> empList = empMapper.list();
		Page<Emp> p = (Page<Emp>) empList;

		//3. 封装分页结果
		return new PageResult<>(p.getTotal(), p.getResult());
	}
}
