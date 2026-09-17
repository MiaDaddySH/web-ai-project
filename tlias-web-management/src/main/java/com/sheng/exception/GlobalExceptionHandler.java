package com.sheng.exception;

import com.sheng.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception exception) {
        log.error("全局异常处理: {}", exception.getMessage());
        return Result.error("对不起，服务器异常，请稍后再试");
    }

    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException exception) {
        log.error("运行时异常处理: {}", exception.getMessage());
        return Result.error(exception.getMessage());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Result handleDuplicateKeyException(DuplicateKeyException exception) {
        log.error("数据重复: {}", exception.getMessage());

        String message = exception.getMessage();
        int i = message.indexOf("Duplicate entry");
        String errMsg = message.substring(i);
        String[] arr = errMsg.split(" ");
        return Result.error(arr[2] + "已存在");
    }
    @ExceptionHandler(FileStorageException.class)
    public Result handleFileStorageException(FileStorageException exception) {
        log.warn("文件上传失败: {}", exception.getMessage());
        return Result.error(exception.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result handleMaxUploadSizeExceededException(MaxUploadSizeExceededException exception) {
        log.warn("上传文件大小超过限制", exception);
        return Result.error("上传文件大小超过限制");
    }
}
