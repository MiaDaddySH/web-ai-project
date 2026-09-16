package com.sheng.service.impl;

import com.sheng.exception.FileStorageException;
import com.sheng.service.UploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class LocalUploadServiceImpl implements UploadService {
    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of("jpg", "jpeg", "png", "gif", "webp");

    private final Path uploadDirectory;

    public LocalUploadServiceImpl(@Value("${app.upload-dir:uploads}") String uploadDirectory) {
        this.uploadDirectory = Path.of(uploadDirectory).toAbsolutePath().normalize();
    }

    @Override
    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = StringUtils.getFilenameExtension(originalFilename);
        if (!StringUtils.hasText(extension)) {
            throw new FileStorageException("上传文件缺少扩展名");
        }

        extension = extension.toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new FileStorageException("仅支持 jpg、jpeg、png、gif 和 webp 图片");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new FileStorageException("上传文件不是有效的图片类型");
        }

        String storedFilename = UUID.randomUUID() + "." + extension;
        Path target = uploadDirectory.resolve(storedFilename).normalize();
        if (!target.getParent().equals(uploadDirectory)) {
            throw new FileStorageException("上传文件名不合法");
        }

        try {
            Files.createDirectories(uploadDirectory);
            file.transferTo(target);
            return storedFilename;
        } catch (IOException | IllegalStateException exception) {
            throw new FileStorageException("文件保存失败", exception);
        }
    }
}
