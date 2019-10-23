package com.bank.payment;


import com.bank.DateTime;
import com.bank.OkError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collection;

import static com.bank.payment.PaymentStatus.CANCELLED;
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
            if (!paymentNotifier.notifyExternalServiceSuccessful(request.getPaymentType().value)) {
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
        if (!payment.getCreatedAt().toLocalDate().equals(DateTime.now().toLocalDate())) {
            throw new PaymentException("Only payments created today can be cancelled!");
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
        BigDecimal cancellationFee = paymentRepository.findCancellationFeeById(paymentId);
        return PaymentResponse.builder()
                .paymentId(paymentId)
                .cancellationFee(cancellationFee)
                .build();
    }

    private BigDecimal calculateFee(Payment payment) {
        long hours = Duration.between(payment.getCreatedAt(), DateTime.now()).toHours();
        return payment.getPaymentType().coefficient.multiply(BigDecimal.valueOf(hours));
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

        if (request.getPaymentType().equals(PaymentType.TYPE1)) {
            if (!"EUR".equals(request.getCurrency()))
                return new OkError("1100", "invalid_currency");
            if (isEmpty(request.getDetails()))
                return new OkError("1101", "invalid_details");
            if (!isEmpty(request.getCreditorBankBIC()))
                return new OkError("1102", "bic_not_allowed");
        }
        if (request.getPaymentType().equals(PaymentType.TYPE2)) {
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
