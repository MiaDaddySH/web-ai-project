package com.sheng.mapper;

import com.sheng.pojo.Dept;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DeptMapper {
	@Select("select id, name, create_time, update_time from dept order by update_time desc")
	List<Dept> findAll();

	@Select("select id, name, create_time, update_time from dept where id = #{id}")
	Dept findDeptById(Integer id);

	@Delete("delete from dept where id = #{id}")
	void deleteDeptById(Integer id);

	@Insert("insert into dept (name, create_time, update_time) values (#{name}, #{createTime}, #{updateTime})")
	void addDept(Dept dept);

	@Update("update dept set name = #{name}, update_time = #{updateTime} where id = #{id}")
	void updateDept(Dept dept);
}
