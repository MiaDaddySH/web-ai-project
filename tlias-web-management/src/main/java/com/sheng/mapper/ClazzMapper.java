package com.sheng.mapper;

import com.sheng.pojo.Clazz;
import com.sheng.pojo.ClazzQueryParam;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClazzMapper {
	List<Clazz> list(ClazzQueryParam clazzQueryParam);

	void insert(Clazz clazz);

	Clazz getById(Integer id);

	int update(Clazz clazz);

	@Delete("DELETE FROM clazz WHERE id = #{id}")
	void delete(Integer id);

	@Select("SELECT * FROM clazz")
	List<Clazz> findAll();
}
