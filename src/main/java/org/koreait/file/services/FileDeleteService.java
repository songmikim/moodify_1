package org.koreait.file.services;

import lombok.RequiredArgsConstructor;
import org.koreait.file.constants.FileStatus;
import org.koreait.file.entities.FileInfo;
import org.koreait.file.repositories.FileInfoRepository;
import org.koreait.global.exceptions.UnAuthorizedException;
import org.koreait.member.entities.Member;
import org.koreait.member.libs.MemberUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Lazy
@Service
@RequiredArgsConstructor
public class FileDeleteService {
    private final FileInfoService infoService;
    private final FileInfoRepository repository;
    private final MemberUtil memberUtil;

    /**
     * 파일 등록번호로 삭제 처리
     *
     * @param seq
     * @return 삭제된 파일 정보
     */
    public FileInfo process(Long seq) {
        FileInfo item = infoService.get(seq);

        // 파일 삭제 권한 체크 S
        String createdBy = item.getCreatedBy();
        Member member = memberUtil.getMember(); // 로그인 사용자 정보
        if (!memberUtil.isAdmin() && memberUtil.isLogin() && StringUtils.hasText(createdBy) && !createdBy.equals(member.getEmail())) {
            // 회원이 올린 파일인 경우 로그인 사용자의 이메일과 일치하는지 체크하고 일치하지 않으면 UnAuthorizedException을 발생, 단, 관리자는 모두 가능
            throw new UnAuthorizedException();
        }

        // 파일 삭제 권한 체크 E

        // 파일 삭제
        File file = new File(item.getFilePath());
        if (file.exists()) {
            file.delete();
        }

        // DB 기록을 삭제
        repository.delete(item);
        repository.flush();

        return item;
    }

    /**
     * 파일 목록 삭제
     *
     * @param gid
     * @param location
     * @return
     */
    public List<FileInfo> process(String gid, String location) {
        List<FileInfo> items = infoService.getList(gid, location, FileStatus.ALL);
        List<FileInfo> deletedItems = new ArrayList<>();
        for (FileInfo item : items) {
            try {
                process(item.getSeq());
                deletedItems.add(item); // 삭제된 파일 정보
            } catch (Exception e) {}
        }

        return deletedItems;
    }

    public List<FileInfo> process(String gid) {
        return process(gid, null);
    }
}
