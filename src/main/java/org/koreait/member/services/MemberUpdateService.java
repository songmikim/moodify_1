package org.koreait.member.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.global.exceptions.script.AlertException;
import org.koreait.global.libs.Utils;
import org.koreait.member.entities.Member;
import org.koreait.member.repositories.MemberRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Lazy
@Service
@RequiredArgsConstructor
public class MemberUpdateService {
    private final Utils utils;
    private final HttpServletRequest request;
    private final MemberRepository repository;

    public void processBatch(List<Integer> chks) {
        if (chks == null || chks.isEmpty()) {
            throw new AlertException("처리할 회원을 선택하세요.", HttpStatus.BAD_REQUEST);
        }

        String method = request.getMethod();
        List<Member> members = new ArrayList<>(); // 수정할 회원 정보를 추가
        for (int chk : chks) {
            Long seq = Long.valueOf(utils.getParam("seq_" + chk));
            Member member = repository.findById(seq).orElse(null);
            if (member == null) continue;
            if (method.equalsIgnoreCase("DELETE")) { // 탈퇴 처리
                member.setDeletedAt(LocalDateTime.now());
            } else { // 수정처리
                boolean updateCredentialAt = Boolean.parseBoolean(Objects.requireNonNullElse(utils.getParam("updateCredentialAt_" + chk), "false"));
                if (updateCredentialAt) { // 비밀번호 변경일시 업데이트
                    member.setCredentialChangedAt(LocalDateTime.now());
                }

                // 탈퇴 취소 처리
                boolean cancelResign = Boolean.parseBoolean(Objects.requireNonNullElse(utils.getParam("cancelResign_" + chk), "false"));
                if (cancelResign) {
                    member.setDeletedAt(null);
                }

            }

            members.add(member);
        }

        repository.saveAllAndFlush(members);
    }
}
