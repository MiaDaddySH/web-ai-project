package com.sheng.controller;

import com.sheng.pojo.*;
import com.sheng.service.ClazzService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/clazzs")
public class ClazzController {
	private final ClazzService clazzService;
	public ClazzController(ClazzService clazzService) {
		this.clazzService = clazzService;
	}

	//班级的page查询
	@GetMapping
	public Result page(ClazzQueryParam clazzQueryParam){
		log.info("分页查询班级信息: {}", clazzQueryParam);
		PageResult<Clazz> pageResult = clazzService.page(clazzQueryParam);
		return Result.success(pageResult);
	}

	//新增班级
	@PostMapping
	public Result add(@RequestBody Clazz clazz){
		log.info("新增班级: {}", clazz);
		clazzService.add(clazz);
		return Result.success();
	}
}
