package com.sheng.service;

import com.sheng.pojo.OperateLog;
import com.sheng.pojo.PageResult;

public interface LogService {
	PageResult<OperateLog> page(Integer page, Integer pageSize);
}
