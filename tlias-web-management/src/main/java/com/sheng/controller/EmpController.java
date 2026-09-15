package com.sheng.controller;

import com.sheng.pojo.Emp;
import com.sheng.pojo.EmpQueryParam;
import com.sheng.pojo.PageResult;
import com.sheng.pojo.Result;
import com.sheng.service.EmpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
