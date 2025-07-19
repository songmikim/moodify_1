package org.koreait.member.repositories;

import org.koreait.member.entities.Member;
import org.koreait.member.social.constants.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, QuerydslPredicateExecutor<Member> {
    boolean existsByEmail(String email);
    Optional<Member> findByEmail(String email);

    boolean existsBySocialTypeAndSocialToken(SocialType type, String token);

    Member findBySocialTypeAndSocialToken(SocialType type, String token);
}
