package com.sheng.service.impl;

import com.sheng.mapper.EmpMapper;
import com.sheng.mapper.StudentMapper;
import com.sheng.pojo.ClazzOption;
import com.sheng.pojo.ValueOption;
import com.sheng.pojo.JobOption;
import com.sheng.service.ReportService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {
	private final EmpMapper empMapper;
	private final StudentMapper studentMapper;
	public ReportServiceImpl(EmpMapper empMapper, StudentMapper studentMapper) {
		this.empMapper = empMapper;
		this.studentMapper = studentMapper;
	}

	@Override
	public JobOption empJobData() {
		List<Map<String, Object>> list = empMapper.countEmpJobData();
		List<Object> jobList = list.stream().map(dataMap->dataMap.get("job")).toList();
		List<Object> dataList = list.stream().map(dataMap->dataMap.get("num")).toList();
		return new JobOption(jobList, dataList);
	}

	@Override
	public List<ValueOption> empGenderData() {
		return empMapper.countEmpGenderData();
	}

	@Override
	public ClazzOption studentCountData() {
		List<Map<String, Object>> list = studentMapper.studentCountData();
		List<Object> clazzList = list.stream().map(dataMap->dataMap.get("clazz")).toList();
		List<Object> dataList = list.stream().map(dataMap->dataMap.get("num")).toList();
		return new ClazzOption(clazzList, dataList);
	}

	@Override
	public List<ValueOption> studentDegreeData() {
		return studentMapper.studentDegreeData();
	}
}
