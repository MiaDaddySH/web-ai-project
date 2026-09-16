package com.sheng.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务。
 */
public interface UploadService {

    /**
     * 保存上传的图片。
     *
     * @param file 上传文件
     * @return 保存后的文件名
     */
    String uploadImage(MultipartFile file);
}
