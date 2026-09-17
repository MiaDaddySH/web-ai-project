package com.sheng.mapper;

import com.sheng.pojo.Student;
import com.sheng.pojo.StudentQueryParam;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StudentMapper {
	List<Student> page(StudentQueryParam queryParam);

	void insert(Student student);

	Student findById(Integer id);

	void update(Student student);

	@Delete("delete from student where id = #{id}")
	void deleteById(Integer id);
}
