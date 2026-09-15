package com.sheng.mapper;

import com.sheng.pojo.Emp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
/**
 * 员工信息
 */
@Mapper
public interface EmpMapper {
	/**
	 * 查询员工信息
	 * @return 员工信息列表
	 */
	@Select("select emp.*, dept.name as dept_name from emp left join dept on emp.dept_id = dept.id")
	public List<Emp> list();
}
