package org.koreait.file.services;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.koreait.file.entities.FileInfo;
import org.koreait.file.exceptions.FileNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Lazy
@Service
@RequiredArgsConstructor
public class FileDownloadService {
    private final FileInfoService infoService;
    private final HttpServletResponse response;

    public void process(Long seq) {
        // 파일 정보 조회
        FileInfo item = infoService.get(seq);

        // 파일이 서버에 있는지 체크
        File file = new File(item.getFilePath());
        if (!file.exists()) { // 파일이 없는 경우
            throw new FileNotFoundException();
        }

        // 파일명 처리 - 윈도우 10버전 이하, 멀티바이트(한글)가 2바이트인 경우 깨짐 방지
        String fileName = new String(item.getFileName().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

        // 파일 형식
        String contentType = item.getContentType();
        contentType = StringUtils.hasText(contentType) ? contentType : "application/octet-stream";

        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            // 헤더 출력
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.setContentType(contentType);
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setIntHeader("Expires", 0);
            response.setContentLengthLong(file.length());

            // 바디 출력
            OutputStream out = response.getOutputStream();
            out.write(bis.readAllBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
