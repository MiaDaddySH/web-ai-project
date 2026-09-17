package com.sheng.service;

import com.sheng.pojo.ClazzOption;
import com.sheng.pojo.ValueOption;
import com.sheng.pojo.JobOption;

import java.util.List;

public interface ReportService {

	JobOption empJobData();

	List<ValueOption> empGenderData();

	ClazzOption studentCountData();

	List<ValueOption> studentDegreeData();
}
