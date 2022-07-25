package com.foryou.matchingservice.api.repository;

import com.foryou.matchingservice.api.dto.response.Response;
import com.foryou.matchingservice.api.enums.OttType;
import com.foryou.matchingservice.api.enums.PartyRole;
import com.foryou.matchingservice.api.enums.StatusType;

import java.util.List;

public interface InitRepository {

    List<Long> selectUnprocessedWait(OttType ott, PartyRole role);

    List<Response> selectUnprocessedAfterWait(StatusType status);
}
