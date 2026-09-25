package com.sheng.service.impl;

import com.sheng.exception.FileStorageException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 本地图片上传服务的单元测试。
 *
 * <p>此处既验证成功写盘，也验证文件名、扩展名和 Content-Type 的防御性检查。</p>
 */
class LocalUploadServiceImplTest {

    // JUnit 为每个测试创建独立临时目录，执行后自动清理，不会污染项目 uploads 目录。
    @TempDir
    Path tempDirectory;

    @Test
    void shouldStoreImageWithGeneratedFilename() throws Exception {
        LocalUploadServiceImpl service = new LocalUploadServiceImpl(tempDirectory.toString());
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.PNG", "image/png", "image-content".getBytes());

        String storedFilename = service.uploadImage(file);

        // 同时校验扩展名归一化、文件存在和写入内容，避免只验证返回值造成假成功。
        assertTrue(storedFilename.endsWith(".png"));
        assertTrue(Files.exists(tempDirectory.resolve(storedFilename)));
        assertEquals("image-content", Files.readString(tempDirectory.resolve(storedFilename)));
    }

    @Test
    void shouldRejectEmptyFile() {
        LocalUploadServiceImpl service = new LocalUploadServiceImpl(tempDirectory.toString());
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.png", "image/png", new byte[0]);

        FileStorageException exception = assertThrows(
                FileStorageException.class, () -> service.uploadImage(file));

        assertEquals("上传文件不能为空", exception.getMessage());
    }

    @Test
    void shouldRejectUnsupportedExtension() {
        LocalUploadServiceImpl service = new LocalUploadServiceImpl(tempDirectory.toString());
        MockMultipartFile file = new MockMultipartFile(
                "file", "script.exe", "application/octet-stream", "content".getBytes());

        // 即使上传参数合法，服务端仍必须使用白名单阻止可执行文件。
        assertThrows(FileStorageException.class, () -> service.uploadImage(file));
    }

    @Test
    void shouldRejectFileWithoutExtension() {
        LocalUploadServiceImpl service = new LocalUploadServiceImpl(tempDirectory.toString());
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar", "image/png", "content".getBytes());

        FileStorageException exception = assertThrows(
                FileStorageException.class, () -> service.uploadImage(file));

        assertEquals("上传文件缺少扩展名", exception.getMessage());
    }

    @Test
    void shouldRejectNonImageContentType() {
        LocalUploadServiceImpl service = new LocalUploadServiceImpl(tempDirectory.toString());
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.png", "text/plain", "content".getBytes());

        // 不能只相信 .png 后缀，还要检查声明的媒体类型。
        FileStorageException exception = assertThrows(
                FileStorageException.class, () -> service.uploadImage(file));

        assertEquals("上传文件不是有效的图片类型", exception.getMessage());
    }
}
