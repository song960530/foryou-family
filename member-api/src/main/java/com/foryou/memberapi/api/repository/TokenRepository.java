package com.foryou.memberapi.api.repository;

import com.foryou.memberapi.api.entity.Member;
import com.foryou.memberapi.api.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByMember(Member member);

    @Query("select t " +
            "from Token t inner join Member m on t.member.no = m.no " +
            "where m.memberId =:memberId " +
            "and t.accessToken=:accessToken " +
            "and t.refreshToken=:refreshToken")
    Optional<Token> findByAllColumn(@Param("memberId") String memberId, @Param("accessToken") String accessToken, @Param("refreshToken") String refreshToken);
}
