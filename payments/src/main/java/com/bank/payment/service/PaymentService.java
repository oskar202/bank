package com.bank.payment.service;


import com.bank.DateTime;
import com.bank.OkError;
import com.bank.payment.PaymentException;
import com.bank.payment.PaymentType;
import com.bank.payment.entity.Payment;
import com.bank.payment.entity.PaymentCancelResponse;
import com.bank.payment.entity.PaymentCreationRequest;
import com.bank.payment.entity.PaymentResponse;
import com.bank.payment.repository.NotificationRepository;
import com.bank.payment.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collection;

import static com.bank.payment.PaymentStatus.CANCELLED;
import static com.bank.payment.PaymentType.TYPE1;
import static com.bank.payment.PaymentType.TYPE2;
import static org.springframework.util.StringUtils.isEmpty;

@Service
@Slf4j
public class PaymentService {

    private PaymentRepository paymentRepository;
    private final PaymentNotifier paymentNotifier;
    private final NotificationRepository notificationRepository;

    public PaymentService(PaymentRepository paymentRepository, PaymentNotifier paymentNotifier, NotificationRepository notificationRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentNotifier = paymentNotifier;
        this.notificationRepository = notificationRepository;
    }

    public OkError createPayment(PaymentCreationRequest request) {
        OkError okError = hasErrors(request);
        if (okError == null) {
            paymentRepository.createNewPayment(request);
            PaymentType paymentType = request.getPaymentType();
            if ((paymentType.equals(TYPE1) || paymentType.equals(TYPE2))
                    && !paymentNotifier.notifyExternalServiceSuccessful(paymentType.getValue())) {
                notificationRepository.saveUnsuccessfulNotify(request);
            }
            return null;
        }
        return okError;
    }

    public PaymentCancelResponse cancelPayment(Long paymentId) {
        Payment payment;
        try {
            payment = paymentRepository.findOneNotCancelled(paymentId);
        } catch (Exception e) {
            throw new PaymentException("Not possible to cancel payment with id: " + paymentId);
        }
        if (payment == null || !payment.getCreatedAt().toLocalDate().equals(DateTime.now().toLocalDate())) {
            throw new PaymentException("Only not already cancelled payments that are created today can be cancelled!");
        }

        BigDecimal fee = calculateFee(payment);
        payment.setCancellationFee(fee);
        payment.setPaymentStatus(CANCELLED);
        paymentRepository.cancelPayment(payment);

        return PaymentCancelResponse.builder().cancellationFee(fee).currency(payment.getCurrency()).build();
    }

    public Collection<Long> getAllNotCancelledSortedPaymentIds() {
        return paymentRepository.findAllNotCancelledSortedByAmount();
    }

    public PaymentResponse getCancelledPaymentById(Long paymentId) {
        BigDecimal cancellationFee;
        try {
            cancellationFee = paymentRepository.findCancellationFeeById(paymentId);
        } catch (Exception e) {
            throw new PaymentException("Payment not found!");
        }
        return PaymentResponse.builder()
                .paymentId(paymentId)
                .cancellationFee(cancellationFee)
                .build();
    }

    private BigDecimal calculateFee(Payment payment) {
        long hours = Duration.between(payment.getCreatedAt(), DateTime.now()).toHours();
        return payment.getPaymentType().getCoefficient().multiply(BigDecimal.valueOf(hours));
    }

    private OkError hasErrors(PaymentCreationRequest request) {
        if (request == null)
            return new OkError("1001", "invalid_request");
        if (request.getAmount().compareTo(new BigDecimal(0)) < 1)
            return new OkError("1002", "invalid_amount");
        if (isEmpty(request.getCurrency()))
            return new OkError("1003", "invalid_currency");
        if (isEmpty(request.getDebtorIban()))
            return new OkError("1004", "invalid_debtor_iban");
        if (isEmpty(request.getCreditorIban()))
            return new OkError("1005", "invalid_creditor_iban");

        if (request.getPaymentType().equals(TYPE1)) {
            if (!"EUR".equals(request.getCurrency()))
                return new OkError("1100", "invalid_currency");
            if (isEmpty(request.getDetails()))
                return new OkError("1101", "invalid_details");
            if (!isEmpty(request.getCreditorBankBIC()))
                return new OkError("1102", "bic_not_allowed");
        }
        if (request.getPaymentType().equals(TYPE2)) {
            if (!"USD".equals(request.getCurrency()))
                return new OkError("1200", "invalid_currency");
            if (!isEmpty(request.getCreditorBankBIC()))
                return new OkError("1201", "bic_not_allowed");
        }
        if (request.getPaymentType().equals(PaymentType.TYPE3)) {
            if (!"EUR".equals(request.getCurrency()) && !"USD".equals(request.getCurrency()))
                return new OkError("1300", "invalid_currency");
            if (isEmpty(request.getCreditorBankBIC()))
                return new OkError("1301", "invalid_bic");
        }
        return null;
    }
}
