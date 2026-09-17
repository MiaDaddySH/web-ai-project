package com.sheng.controller;

import com.sheng.pojo.*;
import com.sheng.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

	@PostMapping
	public Result add(@RequestBody Student student){
		log.info("添加学生信息: {}", student);
		studentService.add(student);
		return Result.success();
	}
	//find a student by id
	@GetMapping("/{id}")
	public Result findById(@PathVariable Integer id){
		log.info("查询学生信息: {}", id);
		Student student = studentService.findById(id);
		return Result.success(student);
	}
}
