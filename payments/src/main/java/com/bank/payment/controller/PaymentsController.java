package com.bank.payment.controller;

import com.bank.OkError;
import com.bank.payment.entity.PaymentCancelResponse;
import com.bank.payment.entity.PaymentCreationRequest;
import com.bank.payment.entity.PaymentResponse;
import com.bank.payment.service.PaymentService;
import com.bank.utils.ClientCountry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(value = "v2/payment")
@Slf4j
public class PaymentsController {

    private final PaymentService paymentService;
    private final ClientCountry clientCountry;

    public PaymentsController(PaymentService paymentService, ClientCountry clientCountry) {
        this.paymentService = paymentService;
        this.clientCountry = clientCountry;
    }

    @PostMapping("create-payment")
    public ResponseEntity createPayment(@RequestBody PaymentCreationRequest request) {
        log.info("create-payment endpoint accessed from: " + clientCountry.getUserLocationByIp());
        OkError result = paymentService.createPayment(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("get-payments")
    public ResponseEntity getPayments() {
        log.info("get-payments endpoint accessed from: " + clientCountry.getUserLocationByIp());
        Collection<Long> response = paymentService.getAllNotCancelledSortedPaymentIds();
        return ResponseEntity.ok(response);
    }

    @GetMapping("get-payment/{paymentId}")
    public ResponseEntity getPaymentById(@PathVariable Long paymentId) {
        log.info("get-payment/" + paymentId + " endpoint accessed from: " + clientCountry.getUserLocationByIp());
        PaymentResponse response = paymentService.getCancelledPaymentById(paymentId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("cancel-payment")
    public ResponseEntity postPayment(@RequestBody Long paymentId) {
        log.info("cancel-payment endpoint accessed from: " + clientCountry.getUserLocationByIp());
        PaymentCancelResponse response = paymentService.cancelPayment(paymentId);
        return ResponseEntity.ok(response);
    }
}
