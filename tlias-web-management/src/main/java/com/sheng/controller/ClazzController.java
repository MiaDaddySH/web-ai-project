package com.sheng.controller;

import com.sheng.pojo.*;
import com.sheng.service.ClazzService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

	//根据id查询班级
	@GetMapping("/{id}")
	public Result getById(@PathVariable Integer id){
		log.info("根据id查询班级: {}", id);
		Clazz clazz = clazzService.getById(id);
		return Result.success(clazz);
	}

	@PutMapping
	public Result update(@RequestBody Clazz clazz){
		log.info("修改班级信息: {}", clazz);
		clazzService.update(clazz);
		return Result.success();
	}

	@DeleteMapping("/{id}")
	public Result delete(@PathVariable Integer id){
		log.info("删除班级: {}", id);
		clazzService.delete(id);
		return Result.success();
	}

	@GetMapping("/list")
	public Result list(){
		log.info("查询所有班级信息");
		List<Clazz> list = clazzService.list();
		return Result.success(list);
	}

}
