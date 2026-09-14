package com.sheng.controller;

import com.sheng.pojo.Dept;
import com.sheng.pojo.Result;
import com.sheng.service.DeptService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/depts")
public class DeptController {

	private final DeptService deptService;

	public DeptController(DeptService deptService) {
		this.deptService = deptService;
	}

	@GetMapping
	public Result findAll() {
		List<Dept> deptList = deptService.findAll();
		return Result.success(deptList);
	}

	@DeleteMapping()
	public Result deleteDeptById(Integer id) {
		deptService.deleteDeptById(id);
		return Result.success();
	}

	@PostMapping()
	public Result addDept(@RequestBody Dept dept) {
		System.out.println(dept);
		deptService.addDept(dept);
		return Result.success();
	}
}
