package com.sheng.controller;

import com.sheng.pojo.Result;
import com.sheng.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
public class UploadController {
	private final UploadService uploadService;

	public UploadController(UploadService uploadService) {
		this.uploadService = uploadService;
	}

	@PostMapping("/upload")
	public Result upload(@RequestParam(value = "name", required = false) String name,
	                     @RequestParam(value = "age", required = false) Integer age,
	                     @RequestParam("file") MultipartFile file) {
		log.info("接收到文件上传请求: name={}, age={}, originalFilename={}, size={}",
				name, age, file.getOriginalFilename(), file.getSize());

		String fileName = uploadService.uploadImage(file);
		return Result.success(fileName);
	}
}
