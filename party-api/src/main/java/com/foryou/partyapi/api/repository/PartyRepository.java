package com.foryou.partyapi.api.repository;

import com.foryou.partyapi.api.dto.response.MyPartyResDto;
import com.foryou.partyapi.api.entity.Party;
import com.foryou.partyapi.api.enums.OttType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PartyRepository extends JpaRepository<Party, Long> {
    @Query("select count(p) > 0 " +
            "from Party p " +
            "where p.memberId=:memberId " +
            "and p.ott=:ott " +
            "and p.leaveYN=false")
    Boolean existOttForMember(String memberId, OttType ott);

    @Query("select new com.foryou.partyapi.api.dto.response.MyPartyResDto(p.ott,p.role,case when p.partyInfo is null then '매칭중' else '매칭완료' end) " +
            "from Party p " +
            "where p.memberId=:memberId " +
            "and p.leaveYN=false"
    )
    List<MyPartyResDto> selectMyPartyList(String memberId);

    @Query("select p " +
            "from Party p " +
            "where p.partyInfo=(select p.partyInfo from Party p where p.no=:no) " +
            "and p.leaveYN = false")
    List<Party> selectSamePartyMember(Long no);
}
