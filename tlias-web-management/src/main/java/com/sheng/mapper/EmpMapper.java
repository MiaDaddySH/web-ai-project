package com.sheng.mapper;

import com.sheng.pojo.Emp;
import com.sheng.pojo.EmpQueryParam;
import com.sheng.pojo.ValueOption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

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
	List<Emp> list(EmpQueryParam empQueryParam);

	/**
	 * 添加员工信息
	 * @param emp 员工信息
	 */
	void insert(Emp emp);

	void deleteByIds(List<Integer> ids);

	Emp getById(Integer id);

	//更新员工中除了工作经历的所有信息
	void updateEmp(Emp emp);

	List<Map<String, Object>> countEmpJobData();

	List<ValueOption> countEmpGenderData();

	@Select("SELECT * FROM emp")
	List<Emp> findAll();

	List<Emp> findEmpByDeptId(Integer deptId);
}
