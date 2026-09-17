package com.sheng.service;

import com.sheng.pojo.PageResult;
import com.sheng.pojo.Student;
import com.sheng.pojo.StudentQueryParam;

import java.util.List;

public interface StudentService {
	PageResult<Student> page(StudentQueryParam queryParam);

	void add(Student student);

	Student findById(Integer id);

	void update(Student student);

	void updateViolationScore(Integer id, Integer score);

	void deleteByBatch(List<Integer> ids);
}
