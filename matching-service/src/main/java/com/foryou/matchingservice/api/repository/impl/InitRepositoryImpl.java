package com.foryou.matchingservice.api.repository.impl;


import com.foryou.matchingservice.api.dto.response.QResponse;
import com.foryou.matchingservice.api.dto.response.Response;
import com.foryou.matchingservice.api.enums.OttType;
import com.foryou.matchingservice.api.enums.PartyRole;
import com.foryou.matchingservice.api.enums.StatusType;
import com.foryou.matchingservice.api.repository.InitRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.foryou.matchingservice.api.entity.QMatch.match;

@Repository
@RequiredArgsConstructor
public class InitRepositoryImpl implements InitRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Long> selectUnprocessedWait(OttType ott, PartyRole role) {
        return queryFactory
                .select(match.no)
                .from(match)
                .where(
                        match.status.eq(StatusType.WAIT)
                        , ottEq(ott)
                        , roleEq(role)
                )
                .fetch();
    }

    @Override
    public List<Response> selectUnprocessedStart() {
        return queryFactory
                .select(new QResponse(
                        match.no
                        , match.linkedNo
                ))
                .from(match)
                .where(
                        match.status.eq(StatusType.START)
                        , match.role.eq(PartyRole.OWNER)
                )
                .fetch();
    }

    private BooleanExpression roleEq(PartyRole role) {
        return role != null ? match.role.eq(role) : null;
    }

    private BooleanExpression ottEq(OttType ott) {
        return ott != null ? match.ott.eq(ott) : null;
    }
}
