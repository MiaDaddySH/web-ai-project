package com.sheng.controller;

import com.sheng.anno.LogOperation;
import com.sheng.pojo.Dept;
import com.sheng.pojo.Result;
import com.sheng.service.DeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/depts")
public class DeptController {
	private final DeptService deptService;

	public DeptController(DeptService deptService) {
		this.deptService = deptService;
	}

	/**
	 * 查询所有部门
	 *
	 * @return 部门列表
	 */
	@GetMapping
	public Result findAll() {
		log.info("查询所有部门");
		List<Dept> deptList = deptService.findAll();
		return Result.success(deptList);
	}

	/**
	 * 根据id查询部门
	 *
	 * @param id 部门id
	 * @return 部门
	 */
	@GetMapping("/{id}")
	public Result findDeptById(@PathVariable Integer id) {
		log.info("根据id:{}查询部门", id);
		Dept dept = deptService.findDeptById(id);
		return Result.success(dept);
	}

	/**
	 * 根据id删除部门
	 *
	 * @param id 部门id
	 * @return 删除结果
	 */
	@LogOperation
	@DeleteMapping()
	public Result deleteDeptById(Integer id) {
		log.info("根据id:{}删除部门", id);
		deptService.deleteDeptById(id);
		return Result.success();
	}

	/**
	 * 添加部门
	 *
	 * @param dept 部门
	 * @return 添加结果
	 */
	@LogOperation
	@PostMapping()
	public Result addDept(@RequestBody Dept dept) {
		log.info("添加部门:{}", dept);
		deptService.addDept(dept);
		return Result.success();
	}

	/**
	 * 修改部门
	 *
	 * @param dept 部门
	 * @return 修改结果
	 */
	@LogOperation
	@PutMapping()
	public Result updateDept(@RequestBody Dept dept) {
		log.info("修改部门:{}", dept);
		deptService.updateDept(dept);
		return Result.success();
	}
}
