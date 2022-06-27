package com.foryoufamily.api.repository;

import com.foryoufamily.api.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByMemberId(String memberId);

    @EntityGraph(attributePaths = {"roles"})
    Optional<Member> findByMemberId(String memberId);
}
