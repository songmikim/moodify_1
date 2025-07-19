package org.koreait.file.services;

import lombok.RequiredArgsConstructor;
import org.koreait.file.constants.FileStatus;
import org.koreait.file.controllers.RequestUpload;
import org.koreait.file.entities.FileInfo;
import org.koreait.file.repositories.FileInfoRepository;
import org.koreait.global.configs.FileProperties;
import org.koreait.global.exceptions.script.AlertBackException;
import org.koreait.global.libs.Utils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Lazy
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(FileProperties.class)
public class FileUploadService {

    private final Utils utils;
    private final FileProperties properties;
    private final FileInfoRepository repository;
    private final FileInfoService infoService;
    private final FileDeleteService deleteService;

    public List<FileInfo> process(RequestUpload form) {
        String gid = form.getGid(); // 그룹 ID
        gid = StringUtils.hasText(gid) ? gid : UUID.randomUUID().toString();
        String location = form.getLocation();
        boolean single = form.isSingle(); // 하나의 파일만 업로드(기존 gid + location으로 등록된 파일을 삭제하고 다시 업로드)
        boolean imageOnly = form.isImageOnly(); // 이미지 형식이 아닌 파일은 업로드 제외

        MultipartFile[] files = form.getFiles();
        if (files == null || files.length == 0) { // 파일을 업로드 하지 않은 경우
            throw new AlertBackException(utils.getMessage("NotUpload.file"), HttpStatus.BAD_REQUEST);
        }

        // 하나의 파일만 업로드 하는 경우
        if (single) {
            // 기존 업로드된 파일 삭제
            deleteService.process(gid, location);

            files = new MultipartFile[] { files[0] };
        }

        // 이미지 형식으로만 한정하는 경우
        if (imageOnly) {
            files = Arrays.stream(files).filter(file -> file.getContentType() != null && file.getContentType().startsWith("image/")).toArray(MultipartFile[]::new);
        }

        String basePath = properties.getPath(); // 파일 업로드 기본 경로

        List<FileInfo> uploadedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            // 1. 업로드한 파일 정보를 DB에 기록 S
            String fileName = file.getOriginalFilename(); // 업로드할때 파일명
            String extension = fileName.substring(fileName.lastIndexOf(".")); // 확장자
            String contentType = file.getContentType();

            FileInfo item = new FileInfo();
            item.setGid(gid);
            if (StringUtils.hasText(location)) {
                item.setLocation(location);
            }

            item.setFileName(fileName);
            item.setExtension(extension);
            item.setContentType(contentType);

            repository.saveAndFlush(item);
            // 1. 업로드한 파일 정보를 DB에 기록 E

            // 2. seq, extension으로 서버에 올릴 경로 만들어주고 업로드 처리
            long seq = item.getSeq();
            long folder = seq % 10L; // 0 ~ 9
            String _fileName = seq + Objects.requireNonNullElse(extension, "");
            String uploadDir = String.format("%s/%s", basePath, folder);
            File _uploadDir = new File(uploadDir);
            if (!_uploadDir.exists() || !_uploadDir.isDirectory()) {
                _uploadDir.mkdir();
            }

            File uploadPath = new File(_uploadDir, _fileName);
            try {
                file.transferTo(uploadPath);
                infoService.addInfo(item);
                uploadedFiles.add(item);
            } catch (IOException e) {
                // 업로드 실패시 저장된 DB 데이터 삭제
                repository.deleteById(seq);
                repository.flush();
                e.printStackTrace();
            }
        }

        return uploadedFiles;
    }

    /**
     * 파일과 관련된 그룹작업 완료시 완료 표기 처리
     * - done : true
     *
     * @param gid
     */
    public void processDone(String gid) {
        List<FileInfo> items = infoService.getList(gid, null, FileStatus.ALL);

        items.forEach(item -> item.setDone(true));

        repository.saveAllAndFlush(items);
    }
}
