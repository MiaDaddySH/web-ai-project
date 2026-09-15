package com.sheng.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sheng.mapper.EmpMapper;
import com.sheng.pojo.Emp;
import com.sheng.pojo.EmpQueryParam;
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
	 * 根据查询条件分页查询员工信息
	 * @param empQueryParam 查询条件
	 * @return 员工列表
	 */
	@Override
	public PageResult<Emp> page(EmpQueryParam empQueryParam) {
		//1. 设置分页参数
		PageHelper.startPage(empQueryParam.getPage(), empQueryParam.getPageSize());

		//2. 执行查询
		List<Emp> empList = empMapper.list(empQueryParam);
		Page<Emp> p = (Page<Emp>) empList;

		//3. 封装分页结果
		return new PageResult<>(p.getTotal(), p.getResult());
	}
}
