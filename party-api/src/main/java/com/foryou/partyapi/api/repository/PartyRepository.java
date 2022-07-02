package com.foryou.partyapi.api.repository;

import com.foryou.partyapi.api.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartyRepository extends JpaRepository<Party, Long> {
    Optional<Party> findByMemberId(String memberId);
}
