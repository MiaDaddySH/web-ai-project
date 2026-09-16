package com.sheng.exception;

import com.sheng.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

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
