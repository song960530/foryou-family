package com.foryou.matchingservice.api.service;

import com.foryou.matchingservice.api.dto.request.MatchingRequestMessage;

import java.util.List;

public interface MatchingService {
    List<Long> createMatch(MatchingRequestMessage request);
}
