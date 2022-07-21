package com.foryou.matchingservice.api.repository;

import com.foryou.matchingservice.api.entity.Match;
import com.foryou.matchingservice.api.enums.StatusType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long>, InitRepository {

    Optional<Match> findByNoAndStatus(Long no, StatusType statusType);
}
