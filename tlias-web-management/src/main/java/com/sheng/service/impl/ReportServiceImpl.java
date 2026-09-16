package com.sheng.service.impl;

import com.sheng.mapper.EmpMapper;
import com.sheng.pojo.GenderOption;
import com.sheng.pojo.JobOption;
import com.sheng.service.ReportService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {
	private final EmpMapper empMapper;
	public ReportServiceImpl(EmpMapper empMapper) {
		this.empMapper = empMapper;
	}

	@Override
	public JobOption empJobData() {
		List<Map<String, Object>> list = empMapper.countEmpJobData();
		List<Object> jobList = list.stream().map(dataMap->dataMap.get("job")).toList();
		List<Object> dataList = list.stream().map(dataMap->dataMap.get("num")).toList();
		return new JobOption(jobList, dataList);
	}

	@Override
	public List<GenderOption> empGenderData() {
		return empMapper.countEmpGenderData();
	}
}
