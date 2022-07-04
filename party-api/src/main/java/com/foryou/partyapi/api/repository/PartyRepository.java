package com.foryou.partyapi.api.repository;

import com.foryou.partyapi.api.entity.Party;
import com.foryou.partyapi.api.enums.OttType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PartyRepository extends JpaRepository<Party, Long> {
    @Query("select count(p) > 0 " +
            "from Party p " +
            "where p.memberId=:memberId " +
            "and p.ott=:ott " +
            "and p.leaveYN=false")
    Boolean existOttForMember(String memberId, OttType ott);
}
