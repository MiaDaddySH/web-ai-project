package com.sheng.mapper;

import com.sheng.pojo.Clazz;
import com.sheng.pojo.ClazzQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ClazzMapper {
	List<Clazz> list(ClazzQueryParam clazzQueryParam);

	void insert(Clazz clazz);

	Clazz getById(Integer id);

	int update(Clazz clazz);
}
