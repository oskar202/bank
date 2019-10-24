package com.bank.payment.controller;

import com.bank.OkError;
import com.bank.payment.entity.PaymentCancelResponse;
import com.bank.payment.entity.PaymentCreationRequest;
import com.bank.payment.entity.PaymentResponse;
import com.bank.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(value = "v1/payments")
public class PaymentsController {

    private final PaymentService paymentService;

    public PaymentsController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<OkError> createPayment(@RequestBody PaymentCreationRequest request) {
        OkError result = paymentService.createPayment(request);
        if (result == null) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping
    public ResponseEntity<Collection<Long>> getPayments() {
        Collection<Long> response = paymentService.getAllNotCancelledSortedPaymentIds();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long paymentId) {
        PaymentResponse response = paymentService.getCancelledPaymentById(paymentId);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("{paymentId}")
    public ResponseEntity<PaymentCancelResponse> cancelPayment(@PathVariable Long paymentId) {
        PaymentCancelResponse response = paymentService.cancelPayment(paymentId);
        return ResponseEntity.ok().body(response);
    }
}
