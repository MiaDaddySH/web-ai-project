package com.sheng.controller;

import com.sheng.pojo.OperateLog;
import com.sheng.pojo.PageResult;
import com.sheng.pojo.Result;
import com.sheng.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/log")
@RestController
@RequiredArgsConstructor
public class LogController {
	private final LogService logService;


	//请求样例/log/page?page=1&pageSize=10
	@GetMapping("/page")
	public Result page(@RequestParam Integer page, @RequestParam Integer pageSize) {
		log.info("分页查询日志信息: {} {}", page, pageSize);
		PageResult<OperateLog> pageResult = logService.page(page, pageSize);
		return Result.success(pageResult);
	}

}
