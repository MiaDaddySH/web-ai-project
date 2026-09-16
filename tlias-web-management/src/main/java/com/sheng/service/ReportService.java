package com.sheng.service;

import com.sheng.pojo.GenderOption;
import com.sheng.pojo.JobOption;

import java.util.List;

public interface ReportService {

	JobOption empJobData();

	List<GenderOption> empGenderData();
}
