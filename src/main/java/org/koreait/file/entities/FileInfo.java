package org.koreait.file.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.koreait.global.entities.BaseEntity;
import org.springframework.data.annotation.CreatedBy;

@Data
@Entity
@Table(indexes = {
        @Index(name="idx_fileinfo_gid1", columnList = "gid,createdAt"),
        @Index(name="idx_fileinfo_gid2", columnList = "gid,done,createdAt"),
        @Index(name="idx_fileinfo_location1", columnList = "gid,location,createdAt"),
        @Index(name="idx_fileinfo_location2", columnList = "gid,location,done,createdAt")
})
public class FileInfo extends BaseEntity {
    @Id
    @GeneratedValue
    private Long seq;

    @Column(length=45, nullable = false)
    private String gid;

    @Column(length=45)
    private String location;

    @Column(length=150, nullable = false)
    private String fileName;

    @Column(length=60)
    private String extension; // 확장자

    @Column(length=100)
    private String contentType; // 파일 종류

    @CreatedBy
    private String createdBy; // 업로드한 로그인 사용자의 이메일

    private boolean done; // 파일 그룹 작업이 완료 되었는지 여부

    @Transient // 엔티티 내부에서 사용할 목적의 필드임을 알려는 애노테이션
    private String filePath; // 파일이 위치한 서버 경로

    @Transient
    private String fileUrl; // 브라우저에서 접근 가능한 URL

    @Transient
    private String thumbBaseUrl;

    @Transient
    private String thumbBasePath;

    @Transient
    private boolean image;


}
