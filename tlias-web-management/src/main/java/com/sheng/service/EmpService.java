package com.sheng.service;

import com.sheng.pojo.Emp;
import com.sheng.pojo.EmpQueryParam;
import com.sheng.pojo.PageResult;

public interface EmpService {

    /**
     * 分页查询
     * @param empQueryParam 查询条件
     * @return 分页结果
     */
    PageResult<Emp> page(EmpQueryParam empQueryParam);

    void add(Emp emp);
}