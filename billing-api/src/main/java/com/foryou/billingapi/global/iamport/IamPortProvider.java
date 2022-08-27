package com.foryou.billingapi.global.iamport;

import com.foryou.billingapi.global.error.CustomException;
import com.foryou.billingapi.global.error.ErrorCode;
import com.foryou.billingapi.global.properties.IamPortProperties;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.OnetimePaymentData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class IamPortProvider {

    @PostConstruct
    protected void init() {
        client = new IamportClient(properties.getApiKey(), properties.getApiSecret());
    }

    private IamportClient client;
    private final IamPortProperties properties;


    public String createAccessToken() {
        String response = null;

        try {
            response = client.getAuth().getResponse().getToken();
        } catch (IamportResponseException e) {
            switch (e.getHttpStatusCode()) {
                case 401:
                    throw new CustomException(ErrorCode.NOT_VALID_IAMPORT_KEY);
                case 500:
                    throw new CustomException(ErrorCode.IAMPORT_SERVER_ERROR);
            }
        } catch (IOException e) {
            throw new CustomException(ErrorCode.IAMPORT_SERVER_ERROR);
        }

        return response;
    }

    public IamportResponse<Payment> pay(OnetimePaymentData onetimePaymentData) {
        IamportResponse<Payment> response = null;

        try {
            response = client.onetimePayment(onetimePaymentData);
        } catch (IamportResponseException e) {
            switch (e.getHttpStatusCode()) {
                case 401:
                    throw new CustomException(ErrorCode.NOT_VALID_IAMPORT_KEY);
                case 500:
                    throw new CustomException(ErrorCode.IAMPORT_SERVER_ERROR);
            }
        } catch (IOException e) {
            throw new CustomException(ErrorCode.IAMPORT_SERVER_ERROR);
        }

        return response;
    }

    public boolean checkResponse(IamportResponse<Payment> response) {
        return (response.getCode() == 0 && "paid".equals(response.getResponse().getStatus()));
    }
}