package com.sheng.mapper;

import com.sheng.pojo.EmpExpr;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmpExprMapper {
	void insertBatch(List<EmpExpr> exprList);
}
