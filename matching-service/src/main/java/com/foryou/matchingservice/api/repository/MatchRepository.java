package com.foryou.matchingservice.api.repository;

import com.foryou.matchingservice.api.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {

}
