package com.sheng.mapper;

import com.sheng.pojo.Student;
import com.sheng.pojo.StudentQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StudentMapper {
	List<Student> page(StudentQueryParam queryParam);
}
