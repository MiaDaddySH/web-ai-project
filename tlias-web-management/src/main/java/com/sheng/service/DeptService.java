package com.sheng.service;

import com.sheng.pojo.Dept;
import org.springframework.stereotype.Service;

import java.util.List;

public interface DeptService {
	List<Dept> findAll();

	void deleteDeptById(Integer id);
}
