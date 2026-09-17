package com.sheng.controller;

import com.sheng.pojo.Emp;
import com.sheng.pojo.LoginResponse;
import com.sheng.pojo.Result;
import com.sheng.service.EmpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/login")
@RestController
public class LoginController {
	private final EmpService empService;

	public LoginController(EmpService empService) {
		this.empService = empService;
	}

	@PostMapping
	public Result login(@RequestBody Emp emp) {
		log.info("login");
		LoginResponse loginResponse = empService.login(emp);
		if (loginResponse == null) {
			return Result.error("用户名或密码错误");
		}
		return Result.success(loginResponse);
	}
}
