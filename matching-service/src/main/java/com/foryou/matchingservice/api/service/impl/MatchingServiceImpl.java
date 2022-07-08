package com.foryou.matchingservice.api.service.impl;

import com.foryou.matchingservice.api.dto.request.MatchingRequestMessage;
import com.foryou.matchingservice.api.repository.MatchRepository;
import com.foryou.matchingservice.api.service.MatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MatchingServiceImpl implements MatchingService {

    private final MatchRepository matchRepository;

    @Override
    @Transactional
    public List<Long> createMatch(MatchingRequestMessage request) {
        return Stream.iterate(0, i -> i + 1)
                .map(i -> request.toEntity())
                .map(entity -> matchRepository.save(entity).getNo())
                .limit(request.getInwon())
                .collect(Collectors.toList());
    }
}
