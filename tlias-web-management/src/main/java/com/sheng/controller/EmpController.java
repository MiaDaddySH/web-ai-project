package com.sheng.controller;

import com.sheng.pojo.Emp;
import com.sheng.pojo.PageResult;
import com.sheng.pojo.Result;
import com.sheng.service.EmpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/emps")
@RestController
public class EmpController {
	private final EmpService empService;

	public EmpController(EmpService empService) {
		this.empService = empService;
	}

	/**
	 * 分页查询
	 * @param page 当前页
	 * @param pageSize 每页大小
	 * @return 分页结果
	 */
	@GetMapping
	public Result page(@RequestParam(defaultValue = "1") Integer page,
	                   @RequestParam(defaultValue = "10") Integer pageSize){
		log.info("分页查询, {},{}", page, pageSize);
		PageResult<Emp> pageResult = empService.page(page,pageSize);
		return Result.success(pageResult);
	}
}
