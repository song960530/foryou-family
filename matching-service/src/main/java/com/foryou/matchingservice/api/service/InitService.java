package com.foryou.matchingservice.api.service;

import com.foryou.matchingservice.api.enums.OttType;
import com.foryou.matchingservice.api.enums.PartyRole;

public interface InitService {
    
    void uploadStartUnprocessData();

    void uploadWaitUnprocessData(OttType ott, PartyRole role);
}
