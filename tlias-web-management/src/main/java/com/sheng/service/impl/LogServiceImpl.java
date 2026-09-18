package com.sheng.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sheng.mapper.EmpLogMapper;
import com.sheng.pojo.OperateLog;
import com.sheng.pojo.PageResult;
import com.sheng.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {
	private final EmpLogMapper empLogMapper;

	@Override
	public PageResult<OperateLog> page(Integer page, Integer pageSize) {
		PageHelper.startPage(page, pageSize);
		List<OperateLog> logList = empLogMapper.list();
		Page<OperateLog> p = (Page<OperateLog>) logList;

		return new PageResult<>(p.getTotal(), p.getResult());

		/**
		 * List<Emp> empList = empMapper.list(empQueryParam);
		 * 		Page<Emp> p = (Page<Emp>) empList;
		 *
		 * 		//3. 封装分页结果
		 * 		return new PageResult<>(p.getTotal(), p.getResult());
		 */
	}
}
