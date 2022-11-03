package com.foryou.matchingservice.api.repository.impl;

import com.foryou.matchingservice.api.dto.response.Response;
import com.foryou.matchingservice.api.entity.Match;
import com.foryou.matchingservice.api.enums.OttType;
import com.foryou.matchingservice.api.enums.PartyRole;
import com.foryou.matchingservice.api.enums.StatusType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("prod")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class InitRepositoryImplTest {

    private InitRepositoryImpl initRepository;
    private JPAQueryFactory queryFactory;
    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        queryFactory = new JPAQueryFactory(em);
        initRepository = new InitRepositoryImpl(queryFactory);
    }

    @Test
    @DisplayName("미처리된 Netflix, Member, Wait 만 검색한다")
    public void selectUnprocessedWaitMemberNetflix() throws Exception {
        // given
        Match member = new Match("member", 1L, 0L, OttType.NETFLIX, PartyRole.MEMBER);
        em.persist(member);

        // when
        List<Long> matchNoList = initRepository.selectUnprocessedWait(OttType.NETFLIX, PartyRole.MEMBER);

        // then
        assertEquals(1, matchNoList.size());
    }

    @Test
    @DisplayName("Netflix, Member, Wait말고 다른 파라미터값으로 조회시 조회되는 데이터가 없다")
    public void justSearchMemberNetflixWait() throws Exception {
        // given
        Match member = new Match("member", 1L, 0L, OttType.NETFLIX, PartyRole.MEMBER);
        em.persist(member);

        // when
        List<Long> matchNoList = initRepository.selectUnprocessedWait(OttType.NETFLIX, PartyRole.OWNER);

        // then
        assertEquals(0, matchNoList.size());
    }

    @Test
    @DisplayName("상태가 START이면서 OWNER인 데이터만 조회한다")
    public void searchStartAndOwner() throws Exception {
        // given
        Match member1 = new Match("member", 1L, 1L, OttType.NETFLIX, PartyRole.OWNER);
        Match member2 = new Match("member", 2L, 2L, OttType.NETFLIX, PartyRole.OWNER);
        Match member3 = new Match("member", 3L, 3L, OttType.NETFLIX, PartyRole.MEMBER);
        member1.changeStatus(StatusType.START);
        member2.changeStatus(StatusType.START);
        member3.changeStatus(StatusType.START);
        member1.link(1L);
        member2.link(2L);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);

        // when
        List<Response> responses = initRepository.selectUnprocessedAfterWait(StatusType.START);

        // then
        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).getMemberPk());
        assertEquals(2L, responses.get(1).getMemberPk());
    }
}