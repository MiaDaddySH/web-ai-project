package com.sheng.controller;

import com.sheng.pojo.JobOption;
import com.sheng.pojo.Result;
import com.sheng.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/report")
@RestController
public class ReportController {
	private final ReportService reportService;
	public ReportController(ReportService reportService) {
		this.reportService = reportService;
	}

	@GetMapping("/empJobData")
	public Result empJobData() {
		log.info("empJobData");

		JobOption jobOption = reportService.empJobData();
		return Result.success(jobOption);
	}

	@GetMapping("/empGenderData")
	public Result empGenderData() {
		log.info("empGenderData");
		return Result.success(reportService.empGenderData());
	}

	@GetMapping("/studentCountData")
	public Result studentCountData() {
		log.info("studentCountData");
		return Result.success(reportService.studentCountData());
	}

	@GetMapping("/studentDegreeData")
	public Result studentDegreeData() {
		log.info("studentDegreeData");
		return Result.success(reportService.studentDegreeData());
	}

}
