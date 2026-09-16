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

class LocalUploadServiceImplTest {

    @TempDir
    Path tempDirectory;

    @Test
    void shouldStoreImageWithGeneratedFilename() throws Exception {
        LocalUploadServiceImpl service = new LocalUploadServiceImpl(tempDirectory.toString());
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.PNG", "image/png", "image-content".getBytes());

        String storedFilename = service.uploadImage(file);

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

        assertThrows(FileStorageException.class, () -> service.uploadImage(file));
    }
}
