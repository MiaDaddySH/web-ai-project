package com.sheng.aop;


import com.sheng.mapper.OperateLogMapper;
import com.sheng.pojo.OperateLog;
import com.sheng.utils.CurrentHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperateLogAspect {

	private final OperateLogMapper operateLogMapper;

	/**
	 * 拦截 controller 包及其子包下，
	 * 所有使用了 @LogOperation 注解的方法。
	 */
	@Around(
			"execution(* com.sheng.controller..*(..)) " +
			"&& @annotation(com.sheng.anno.LogOperation)"
	)
	public Object recordOperateLog(ProceedingJoinPoint joinPoint)
			throws Throwable {

		long beginTime = System.currentTimeMillis();
		Object result = null;
		Throwable businessException = null;

		try {
			// 执行原始的 Controller 方法
			result = joinPoint.proceed();
			return result;
		} catch (Throwable throwable) {
			businessException = throwable;
			throw throwable;
		} finally {
			long costTime =
					System.currentTimeMillis() - beginTime;

			try {
				saveOperateLog(
						joinPoint,
						result,
						costTime,
						businessException
				);
			} catch (Exception logException) {
				// 日志记录失败不能影响正常业务，
				// 也不能覆盖原始业务异常
				log.error("保存操作日志失败", logException);
			}
		}
	}

	private void saveOperateLog(
			ProceedingJoinPoint joinPoint,
			Object result,
			long costTime,
			Throwable businessException
	) {
		OperateLog operateLog = new OperateLog();

		// 操作人
		operateLog.setOperateEmpId(CurrentHolder.getCurrentId());

		// 操作时间
		operateLog.setOperateTime(LocalDateTime.now());

		// 目标类全类名
		operateLog.setClassName(
				joinPoint.getTarget()
				         .getClass()
				         .getName()
		);

		// 目标方法名
		operateLog.setMethodName(joinPoint.getSignature()
		                                  .getName());

		// 运行时参数
		operateLog.setMethodParams(
				Arrays.toString(joinPoint.getArgs())
		);

		// 返回值；执行异常时记录异常信息
		if (businessException == null) {
			operateLog.setReturnValue(result.toString());
		} else {
			operateLog.setReturnValue(
					"执行异常：" + businessException.getMessage()
			);
		}

		// 方法执行耗时
		operateLog.setCostTime(costTime);

		operateLogMapper.insert(operateLog);

		log.info("记录操作日志：{}", operateLog);
	}
}