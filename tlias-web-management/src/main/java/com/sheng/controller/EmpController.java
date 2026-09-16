package com.sheng.controller;

import com.sheng.pojo.Emp;
import com.sheng.pojo.EmpQueryParam;
import com.sheng.pojo.PageResult;
import com.sheng.pojo.Result;
import com.sheng.service.EmpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/emps")
@RestController
public class EmpController {
	private final EmpService empService;

	public EmpController(EmpService empService) {
		this.empService = empService;
	}

	/**
	 * 分页查询员工信息
	 * @param empQueryParam 查询条件
	 * @return 分页结果
	 */

	@GetMapping
	public Result page(EmpQueryParam empQueryParam){
		log.info("分页查询员工信息: {}", empQueryParam);
		PageResult<Emp> pageResult = empService.page(empQueryParam);
		return Result.success(pageResult);
	}

	@GetMapping("/list")
	public Result list(){
		log.info("查询所有员工信息");
		List<Emp> list = empService.list();
		return Result.success(list);
	}

	@PostMapping
	public Result add(@RequestBody Emp emp){
		log.info("添加员工信息: {}", emp);
		empService.add(emp);
		return Result.success();
	}

	@DeleteMapping
	public Result delete(@RequestParam List<Integer> ids){
		log.info("删除员工信息: {}", ids);
		empService.delete(ids);
		return Result.success();
	}

	// 根据id查询员工信息
	@GetMapping("/{id}")
	public Result getById(@PathVariable Integer id){
		log.info("根据id查询员工信息: {}", id);
		Emp emp = empService.getById(id);
		return Result.success(emp);
	}

	@PutMapping
	public Result update(@RequestBody Emp emp){
		log.info("修改员工信息: {}", emp);
		empService.update(emp);
		return Result.success();
	}
}
