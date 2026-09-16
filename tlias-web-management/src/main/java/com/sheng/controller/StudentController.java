package com.sheng.controller;

import com.sheng.pojo.*;
import com.sheng.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/students")
@RestController
public class StudentController {
	private final StudentService studentService;
	public StudentController(StudentService studentService) {
		this.studentService = studentService;
	}


	@GetMapping
	public Result page(StudentQueryParam queryParam){
		log.info("分页查询学生信息: {}", queryParam);
		PageResult<Student> pageResult = studentService.page(queryParam);
		return Result.success(pageResult);
	}

}
