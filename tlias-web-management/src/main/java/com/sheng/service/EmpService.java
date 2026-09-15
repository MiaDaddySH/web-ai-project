package com.sheng.service;

import com.sheng.pojo.Emp;
import com.sheng.pojo.PageResult;
public interface EmpService {

    /**
     * 分页查询
     * @param page 当前页
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<Emp> page(Integer page, Integer pageSize);
}