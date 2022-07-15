package com.foryou.matchingservice.api.service;

import com.foryou.matchingservice.api.dto.response.Response;

public interface ScheduledService {
    Response firstMatchJob(Long ownerPk, Long memberPk);

    Response secondMatchJob(Long ownerPk, Long memberPk);

    void thirdMatchJob(Long ownerPk, Long memberPk);
}
