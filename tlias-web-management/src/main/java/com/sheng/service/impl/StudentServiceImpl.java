package com.sheng.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sheng.mapper.StudentMapper;
import com.sheng.pojo.Clazz;
import com.sheng.pojo.PageResult;
import com.sheng.pojo.Student;
import com.sheng.pojo.StudentQueryParam;
import com.sheng.service.StudentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
	private final StudentMapper studentMapper;
	public StudentServiceImpl(StudentMapper studentMapper) {
		this.studentMapper = studentMapper;
	}

	@Override
	public PageResult<Student> page(StudentQueryParam queryParam) {
		// 设置分页参数
		PageHelper.startPage(queryParam.getPage(), queryParam.getPageSize());

		//执行查询
		List<Student> studentList = studentMapper.page(queryParam);
		Page<Student> p = (Page<Student>) studentList;

		return new PageResult<>(p.getTotal(), p.getResult());
	}

	@Override
	public void add(Student student) {
		LocalDateTime now = LocalDateTime.now();
		student.setCreateTime(now);
		student.setUpdateTime(now);
		studentMapper.insert(student);
	}
}
