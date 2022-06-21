package com.foryoufamily.api.repository;

import com.foryoufamily.api.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest // JPA 관련 컴포넌트만 로드, 테스트 종료 후 자동 롤백
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 저장 테스트")
    public void save() throws Exception {
        // given
        Member member = new Member("test", "password!@#");

        // when
        Member saveMember = memberRepository.save(member);

        // then
        assertNotNull(saveMember);
        assertEquals(1, memberRepository.count());
        assertEquals(member.getId(), saveMember.getId());
        assertEquals(member.getPassword(), saveMember.getPassword());
    }

}