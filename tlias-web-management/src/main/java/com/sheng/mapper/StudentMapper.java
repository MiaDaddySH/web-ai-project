package com.sheng.mapper;

import com.sheng.pojo.Student;
import com.sheng.pojo.StudentQueryParam;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface StudentMapper {
	List<Student> page(StudentQueryParam queryParam);

	void insert(Student student);

	Student findById(Integer id);

	void update(Student student);

	@Update("update student set violation_count = #{violationCount}, " +
			"violation_score = #{violationScore}, update_time = #{updateTime} " +
			"where id = #{id}")
	void updateViolationScore(Student student);

	void deleteByBatch(List<Integer> ids);
}
