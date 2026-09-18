package com.sheng.mapper;

import com.sheng.pojo.EmpLog;
import com.sheng.pojo.OperateLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmpLogMapper {

	@Insert("insert into emp_log (operate_time, info) values (#{operateTime}, #{info})")
	public void insert(EmpLog empLog);

	List<OperateLog> list();
}
