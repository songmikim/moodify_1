package org.koreait.file.services;

import org.junit.jupiter.api.Test;
import org.koreait.file.entities.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class FileInfoServiceTest {

    @Autowired
    private FileInfoService service;

    @Test
    void test() {
        //FileInfo item = service.get(1L);
        //System.out.println(item);
        String gid = "b82530ac-13e5-408e-90c4-29d5bdf89d76";
        List<FileInfo> items = service.getList(gid, null);
        items.forEach(System.out::println);
    }
}
