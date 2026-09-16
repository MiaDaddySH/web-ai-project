package com.sheng.exception;

/**
 * 文件校验或保存失败时抛出的业务异常。
 */
public class FileStorageException extends RuntimeException {

    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
