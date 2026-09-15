package com.sheng.mapper;

import com.sheng.pojo.Emp;
import com.sheng.pojo.EmpQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
/**
 * 员工信息
 */
@Mapper
public interface EmpMapper {
	/**
	 * 根据查询条件分页查询员工信息
	 * @param empQueryParam 查询条件
	 * @return 员工列表
	 */
	public List<Emp> list(EmpQueryParam empQueryParam);
}
