package com.sheng.utils;

public class CurrentHolder {
	private static final ThreadLocal<Integer> CURRENT_LOCAL =
			new ThreadLocal<>();

	/**
	 * 保存当前登录用户 ID
	 */
	public static void setCurrentId(Integer employeeId) {
		CURRENT_LOCAL.set(employeeId);
	}

	/**
	 * 获取当前登录用户 ID
	 */
	public static Integer getCurrentId() {
		return CURRENT_LOCAL.get();
	}

	/**
	 * 清理当前线程中的用户信息
	 */
	public static void remove() {
		CURRENT_LOCAL.remove();
	}

	private CurrentHolder() {
	}
}
