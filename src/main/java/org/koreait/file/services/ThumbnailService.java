package org.koreait.file.services;

import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.koreait.file.controllers.RequestThumb;
import org.koreait.file.entities.FileInfo;
import org.koreait.global.configs.FileProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Objects;

@Lazy
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(FileProperties.class)
public class ThumbnailService {

    private final FileInfoService infoService;
    private final FileProperties properties;

    public String create(RequestThumb form) {
        Long seq = form.getSeq();
        String url = form.getUrl();
        int width = Math.max(form.getWidth(), 50);
        int height = Math.max(form.getHeight(), 50);
        boolean crop = form.isCrop();

        // 썸네일 경로, 이미 생성된 썸네일이 있다면 생성되어 있는 경로로 반환
        String thumbPath = getThumbPath(seq, url, width, height, crop);
        File file = new File(thumbPath);
        if (file.exists()) {
            return thumbPath;
        }

        // 썸네일 생성
        try {
            if (seq != null && seq > 0L) { // 파일 등록번호 기준에서 썸네일 생성
                FileInfo item = infoService.get(seq);
                Thumbnails.Builder<File> builder = Thumbnails.of(item.getFilePath())
                        .size(width, height);
                if (crop) { // 크롭 모드
                    builder.crop(Positions.CENTER);
                }

                builder.toFile(thumbPath);


            } else if (StringUtils.hasText(url)) { // 원격 URL 기준의 썸네일 생성

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return thumbPath;
    }

    public String getThumbPath(Long seq, String url, int width, int height, boolean crop) {
        String basePath = properties.getPath() + "/thumbs";

        String thumbPath = "";
        if (seq != null && seq > 0L) { // 직접 업로드한 파일 기준
            FileInfo item = infoService.get(seq);
            String folder = infoService.folder(seq);
            File file = new File(basePath + "/" + folder);
            if (!file.exists() || !file.isDirectory()) {
                file.mkdirs();
            }

            thumbPath = basePath + String.format("/%s/%s_%s_%s_%s%s", folder, width, height, crop, seq, Objects.requireNonNullElse(item.getExtension(), ""));

        } else if (StringUtils.hasText(url)) { // 원격 URL 이미지인 기준

        }


        return thumbPath;
    }

    /**
     * 파일 등록번호 기준의 썸네일 서버 경로
     *
     * @param seq
     * @param width
     * @param height
     * @return
     */
    public String getThumbPath(Long seq, int width, int height, boolean crop) {
        return getThumbPath(seq, null, width, height, crop);
    }

    /**
     * 원격 주소 기준의 썸네일 서버 경로
     * @param url
     * @param width
     * @param height
     * @return
     */
    public String getThumbPath(String url, int width, int height, boolean crop) {
        return getThumbPath(null, url, width, height, crop);
    }
}
