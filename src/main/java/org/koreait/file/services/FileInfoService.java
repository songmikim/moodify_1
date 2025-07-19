package org.koreait.file.services;

import com.querydsl.core.BooleanBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.file.constants.FileStatus;
import org.koreait.file.entities.FileInfo;
import org.koreait.file.entities.QFileInfo;
import org.koreait.file.exceptions.FileNotFoundException;
import org.koreait.file.repositories.FileInfoRepository;
import org.koreait.global.configs.FileProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(FileProperties.class)
public class FileInfoService {
    private final FileInfoRepository repository;
    private final HttpServletRequest request;
    private final FileProperties properties;

    /**
     * 파일 한개 조회
     *
     * @param seq : 파일 등록번호
     * @return
     */
    public FileInfo get(Long seq) {
        FileInfo item = repository.findById(seq).orElseThrow(FileNotFoundException::new);

        // 추가정보 공통 처리
        addInfo(item);

        return item;
    }

    /**
     * 파일 목록 조회
     *
     * @param gid : 그룹 ID
     * @param location : 그룹 내에서 구분 위치값
     * @return
     */
    public List<FileInfo> getList(String gid, String location, FileStatus status) {
        status = Objects.requireNonNullElse(status, FileStatus.ALL);

        QFileInfo fileInfo = QFileInfo.fileInfo;
        BooleanBuilder andBuilder = new BooleanBuilder();
        andBuilder.and(fileInfo.gid.eq(gid));

        if (StringUtils.hasText(location)) {
            andBuilder.and(fileInfo.location.eq(location));
        }

        if (status != FileStatus.ALL) {
            andBuilder.and(fileInfo.done.eq(status == FileStatus.DONE));
        }

        List<FileInfo> items = (List<FileInfo>)repository.findAll(andBuilder, fileInfo.createdAt.asc());

        // 추가정보공통 처리
        items.forEach(this::addInfo);

        return items;
    }

    public List<FileInfo> getList(String gid, String location) {
        return getList(gid, location, FileStatus.DONE); // 그룹파일 완료 파일만
    }

    public List<FileInfo> getList(String gid) {
        return getList(gid, null);
    }

    /**
     * 추가 정보 처리
     * 1) 파일이 위치하고 있는 서버쪽 경로
     * 2) 브라우저에서 접근 가능한 URL
     * 3) 이미지인 경우 썸네일 이미지 URL
     * @param item
     */
    public void addInfo(FileInfo item) {
        item.setFileUrl(getFileUrl(item));
        item.setFilePath(getFilePath(item));

        /* 파일이 이미지인지 체크 */
        String contentType = item.getContentType();
        item.setImage(StringUtils.hasText(contentType) && contentType.startsWith("image"));

        /* 이미지인 경우 썸네일 기본 URL, 기본 Path  추가 */
        if (item.isImage()) {
            String folder = folder(item);
            String thumbPath = String.format("%s/thumbs/%s/", properties.getPath(), folder);
            String thumbUrl = String.format("%s%s/thumbs/%s/", request.getContextPath(), properties.getUrl(), folder);
            item.setThumbBasePath(thumbPath);
            item.setThumbBaseUrl(thumbUrl);
        }
    }

    public String folder(FileInfo item) {
        long seq = item.getSeq();

        return folder(seq);
    }

    public String folder(long seq) {
        return String.valueOf(seq % 10L); // 0 ~ 9
    }

    // 브라우저에서 접근할 수 있는 URL
    public String getFileUrl(FileInfo item) {
       return String.format("%s%s/%s/%s", request.getContextPath(), properties.getUrl(), folder(item), item.getSeq() + Objects.requireNonNullElse(item.getExtension(), ""));
    }

    // 파일이 위치한 서버 경로
    public String getFilePath(FileInfo item) {
        return String.format("%s/%s/%s", properties.getPath(), folder(item), item.getSeq() + Objects.requireNonNullElse(item.getExtension(), ""));
    }
}
