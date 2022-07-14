package com.foryou.matchingservice.api.service;

import com.foryou.matchingservice.api.dto.response.Response;

public interface ScheduledService {
    Response firstMatchJob(Long ownerNo, Long memberNo);

    Response secondMatchJob(Long ownerPk, Long memberPk);
}
