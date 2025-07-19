package org.koreait.file.services;

import org.junit.jupiter.api.Test;
import org.koreait.file.controllers.RequestUpload;
import org.koreait.file.entities.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@SpringBootTest
public class FileUploadServiceTest {
    @Autowired
    private FileUploadService service;

    @Test
    void test() {
        MockMultipartFile file1 = new MockMultipartFile("file", "test1.png", "image/png", new byte[] {1, 2, 3});
        MockMultipartFile file2 = new MockMultipartFile("file", "test2.png", "image/png", new byte[] {1, 2, 3});

        String gid = UUID.randomUUID().toString();
        String location = "editor";

        RequestUpload form = new RequestUpload();
        form.setGid(gid);
        form.setLocation(location);
        form.setFiles(new MultipartFile[] {file1, file2});

        List<FileInfo> items = service.process(form);
        items.forEach(System.out::println);
    }
}
