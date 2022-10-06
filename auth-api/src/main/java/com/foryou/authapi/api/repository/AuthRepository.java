package com.foryou.authapi.api.repository;

import com.foryou.authapi.api.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByMemberId(String memberId);
}
