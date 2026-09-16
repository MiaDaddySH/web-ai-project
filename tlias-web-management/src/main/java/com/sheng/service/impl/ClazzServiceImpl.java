package com.sheng.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sheng.mapper.ClazzMapper;
import com.sheng.pojo.*;
import com.sheng.service.ClazzService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClazzServiceImpl implements ClazzService {
	private final ClazzMapper clazzMapper;
	public ClazzServiceImpl(ClazzMapper clazzMapper) {
		this.clazzMapper = clazzMapper;
	}

	@Override
	public PageResult<Clazz> page(ClazzQueryParam clazzQueryParam) {
		// 设置分页参数
		PageHelper.startPage(clazzQueryParam.getPage(), clazzQueryParam.getPageSize());

		//执行查询
		List<Clazz> clazzList = clazzMapper.list(clazzQueryParam);
		Page<Clazz> p = (Page<Clazz>) clazzList;

		return new PageResult<>(p.getTotal(), p.getResult());
	}

	@Override
	public void add(Clazz clazz) {
		LocalDateTime now = LocalDateTime.now();
		clazz.setCreateTime(now);
		clazz.setUpdateTime(now);
		clazzMapper.insert(clazz);
	}

	@Override
	public Clazz getById(Integer id) {
		return clazzMapper.getById(id);
	}

	@Override
	public void update(Clazz clazz) {
		clazz.setUpdateTime(LocalDateTime.now());
		clazzMapper.update(clazz);
		int rows = clazzMapper.update(clazz);
		if (rows == 0) {
			throw new RuntimeException("班级不存在");
		}
	}

	@Override
	public void delete(Integer id) {
		clazzMapper.delete(id);
	}

	@Override
	public List<Clazz> list() {
		return clazzMapper.findAll();
	}
}
