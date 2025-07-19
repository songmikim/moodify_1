package org.koreait.member.services;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.global.search.ListData;
import org.koreait.global.search.Pagination;
import org.koreait.member.MemberInfo;
import org.koreait.member.constants.Authority;
import org.koreait.member.controllers.MemberSearch;
import org.koreait.member.entities.Member;
import org.koreait.member.entities.QMember;
import org.koreait.member.repositories.MemberRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

import static org.springframework.data.domain.Sort.Order.desc;

@Lazy
@Service
@RequiredArgsConstructor
public class MemberInfoService implements UserDetailsService {

    private final MemberRepository repository;
    private final HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = repository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));

        Authority authority = Objects.requireNonNullElse(member.getAuthority(), Authority.MEMBER);

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(authority.name()));

        return MemberInfo.builder()
                .email(member.getEmail())
                .password(member.getPassword())
                .member(member)
                .authorities(authorities)
                .build();
    }

    /**
     * 회원 목록
     *
     * @param search
     * @return
     */
    public ListData<Member> getList(MemberSearch search) {
        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();
        limit = limit < 1 ? 20 : limit;

        QMember member = QMember.member;
        BooleanBuilder andBuilder = new BooleanBuilder();
        String sopt = search.getSopt();
        String skey = search.getSkey();
        sopt = StringUtils.hasText(sopt) ? sopt.toUpperCase() : "ALL";
        /**
         * 키워드 검색
         * sopt: 검색 옵션
         *      NAME : 회원명
         *      EMAIL : 이메일
         *      MOBILE : 휴대전화번호
         *      ALL : 통합 검색 - NAME + EMAIL + MOBILE
         */
        if (StringUtils.hasText(skey)) {
            skey = skey.trim();
            StringExpression fields = null;
            if (sopt.equals("NAME")) {
                fields = member.name;
            } else if (sopt.equals("EMAIL")) {
                fields = member.email;
            } else if (sopt.equals("MOBILE")) {
                fields = member.mobile;
            } else {
                fields = member.name.concat(member.email)
                        .concat(member.mobile);
            }
            andBuilder.and(fields.contains(skey));
        }

        // 권한 조건 검색 S
        List<Authority> authorities = search.getAuthority();
        if (authorities != null && !authorities.isEmpty()) {
            andBuilder.and(member.authority.in(authorities));
        }
        // 권한 조건 검색 E

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(desc("createdAt")));

        Page<Member> data = repository.findAll(andBuilder, pageable);
        List<Member> items = data.getContent();
        long total = data.getTotalElements();
        Pagination pagination = new Pagination(page, (int)total, 10, limit, request);


        return new ListData<>(items, pagination);
    }
}
