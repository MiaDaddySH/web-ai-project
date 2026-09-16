package com.sheng.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sheng.mapper.EmpExprMapper;
import com.sheng.mapper.EmpMapper;
import com.sheng.pojo.*;
import com.sheng.service.EmpLogService;
import com.sheng.service.EmpService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmpServiceImpl implements EmpService {
	private final EmpMapper empMapper;
	private final EmpExprMapper empExprMapper;

	private final EmpLogService empLogService;

	public EmpServiceImpl(EmpMapper empMapper, EmpExprMapper empExprMapper, EmpLogService empLogService) {
		this.empMapper = empMapper;
		this.empExprMapper = empExprMapper;
		this.empLogService = empLogService;
	}

	/**
	 * 根据查询条件分页查询员工信息
	 * @param empQueryParam 查询条件
	 * @return 员工列表
	 */
	@Override
	public PageResult<Emp> page(EmpQueryParam empQueryParam) {
		//1. 设置分页参数
		PageHelper.startPage(empQueryParam.getPage(), empQueryParam.getPageSize());

		//2. 执行查询
		List<Emp> empList = empMapper.list(empQueryParam);
		Page<Emp> p = (Page<Emp>) empList;

		//3. 封装分页结果
		return new PageResult<>(p.getTotal(), p.getResult());
	}

	//事务管理,默认情况是发生RuntimeException的时候才会回滚。
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void add(Emp emp) {
		try {
			//1. 保存员工基本信息
			emp.setCreateTime(LocalDateTime.now());
			emp.setUpdateTime(LocalDateTime.now());
			empMapper.insert(emp);

			//2. 保存员工工作经历信息
			List<EmpExpr> exprList = emp.getExprList();
			if (exprList != null && !exprList.isEmpty()) {
				exprList.forEach(expr -> {
					expr.setEmpId(emp.getId());
				});
				empExprMapper.insertBatch(exprList);
			}
		} finally {
			//3. 记录操作日志,不管有没有成功保存员工信息，都记录数据库日志。只为了功能测试。
			EmpLog empLog = new EmpLog(null, LocalDateTime.now(), "添加员工信息: " + emp);
			empLogService.insertLog(empLog);
		}
	}

	/**
	 * 根据员工id删除员工信息
	 * @param ids 员工id列表
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void delete(List<Integer> ids) {
		empMapper.deleteByIds(ids);
		empExprMapper.deleteByEmpIds(ids);
	}

	/**
	 * 根据id获取员工信息
	 * @param id 员工id
	 * @return 员工信息
	 */
	@Override
	public Emp getById(Integer id) {
		return empMapper.getById(id);
	}

	/**
	 * 更新员工信息
	 * @param emp 员工信息
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void update(Emp emp) {
		/*更新员工信息*/
		emp.setUpdateTime(java.time.LocalDateTime.now());
		empMapper.updateEmp(emp);

		/*更新员工工作经历，采用删除再添加的方式*/
		empExprMapper.deleteByEmpIds(List.of(emp.getId()));
		if (emp.getExprList() != null && !emp.getExprList().isEmpty()) {
			emp.getExprList().forEach(expr -> {
				expr.setEmpId(emp.getId());
			});
			empExprMapper.insertBatch(emp.getExprList());
		}
	}

	@Override
	public List<Emp> list() {
		return empMapper.findAll();
	}
}

