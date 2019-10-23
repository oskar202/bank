package com.bank.payment.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpMethod.GET;

@Service
public class PaymentNotifier {

    @Value("${host.fullAddress.audit}")
    private String auditFullAddress;

    boolean notifyExternalServiceSuccessful(String type) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> exchange = restTemplate.exchange(auditFullAddress + "/audit-payment/" + type, GET, null, String.class);
            return HttpStatus.valueOf(exchange.getStatusCodeValue()).is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
}
