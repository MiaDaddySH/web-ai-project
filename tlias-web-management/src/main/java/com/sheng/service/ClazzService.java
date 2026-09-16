package com.sheng.service;

import com.sheng.pojo.Clazz;
import com.sheng.pojo.ClazzQueryParam;
import com.sheng.pojo.PageResult;

import java.util.List;

public interface ClazzService {
	PageResult<Clazz> page(ClazzQueryParam clazzQueryParam);

	void add(Clazz clazz);

	Clazz getById(Integer id);

	void update(Clazz clazz);

	void delete(Integer id);

	List<Clazz> list();
}
