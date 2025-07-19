package org.koreait.member.repositories;

import org.junit.jupiter.api.Test;
import org.koreait.member.entities.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository repository;

    @Test
    void test1() {
        Member member = new Member();
        member.setEmail("user01@test.org");
        member.setPassword("1234");
        member.setName("사용자01");
        member.setMobile("01010001000");
        repository.save(member);


        Member member2 = repository.findById(member.getSeq()).orElse(null);
        System.out.println(member2);
        System.out.println("createdAt:" + member2.getCreatedAt());
    }
}
