package com.foryou.matchingservice.api.service;

import com.foryou.matchingservice.api.dto.request.MatchingRequestMessage;
import com.foryou.matchingservice.api.entity.Match;

import java.util.List;

public interface MatchingService {
    List<Match> createMatch(MatchingRequestMessage request);

    void offerQueue(List<Match> matches);
}
