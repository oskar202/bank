package com.bank.payment;

import com.bank.DateTime;
import com.bank.OkError;
import com.bank.payment.entity.Payment;
import com.bank.payment.entity.PaymentCreationRequest;
import com.bank.payment.entity.PaymentResponse;
import com.bank.payment.repository.NotificationRepository;
import com.bank.payment.repository.PaymentRepository;
import com.bank.payment.service.PaymentNotifier;
import com.bank.payment.service.PaymentService;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import static com.bank.payment.PaymentStatus.CANCELLED;
import static com.bank.payment.PaymentStatus.CREATED;
import static com.bank.payment.PaymentType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class PaymentServiceTest {

    private PaymentRepository paymentRepository = mock(PaymentRepository.class);
    private PaymentNotifier paymentNotifier = mock(PaymentNotifier.class);
    private NotificationRepository notificationRepository = mock(NotificationRepository.class);
    private PaymentService paymentService = new PaymentService(paymentRepository, paymentNotifier, notificationRepository);

    @Before
    public void setUp() {
        DateTime.setMockNow(LocalDateTime.now());
    }

    @Test
    void createPayment_successType1() {
        PaymentCreationRequest request = PaymentCreationRequest.builder()
                .amount(new BigDecimal(1.1))
                .creditorIban("EE9876")
                .currency("EUR")
                .debtorIban("DE1111")
                .paymentType(TYPE1)
                .details("type 1 payment success")
                .build();

        OkError result = paymentService.createPayment(request);

        assertThat(result).isNull();
        verify(paymentRepository).createNewPayment(request);
    }

    @Test
    void createPayment_successType2() {
        PaymentCreationRequest request = PaymentCreationRequest.builder()
                .amount(new BigDecimal(1.1))
                .creditorIban("EE9876")
                .currency("USD")
                .debtorIban("DE1111")
                .paymentType(TYPE2)
                .details("type 2 payment success")
                .build();

        OkError result = paymentService.createPayment(request);

        assertThat(result).isNull();
        verify(paymentRepository).createNewPayment(request);
    }

    @Test
    void createPayment_successType3() {
        PaymentCreationRequest request = PaymentCreationRequest.builder()
                .amount(new BigDecimal(1.1))
                .creditorBankBIC("EE1234")
                .creditorIban("EE9876")
                .currency("EUR")
                .debtorIban("DE1111")
                .paymentType(TYPE3)
                .details("type 3 payment success")
                .build();

        OkError result = paymentService.createPayment(request);

        assertThat(result).isNull();
        verify(paymentRepository).createNewPayment(request);
    }

    @Test
    void createPayment_invalidRequest() {

        OkError result = paymentService.createPayment(null);
        verify(paymentRepository, never()).createNewPayment(any());

        assertThat(result).isNotNull();
        assertThat(result.getErrorMessage()).isEqualTo("invalid_request");
        assertThat(result.getErrorCode()).isEqualTo("1001");
    }

    @Test
    void createPayment_invalidAmount() {
        PaymentCreationRequest request = PaymentCreationRequest.builder()
                .amount(new BigDecimal(-1.2))
                .creditorBankBIC("EE1239")
                .creditorIban("EE9877")
                .currency("EUR")
                .debtorIban("DE1113")
                .paymentType(TYPE3)
                .details("type 3")
                .build();
        OkError result = paymentService.createPayment(request);
        verify(paymentRepository, never()).createNewPayment(any());

        assertThat(result).isNotNull();
        assertThat(result.getErrorMessage()).isEqualTo("invalid_amount");
        assertThat(result.getErrorCode()).isEqualTo("1002");
    }

    @Test
    void createPayment_invalidCurrency() {
        PaymentCreationRequest request = PaymentCreationRequest.builder()
                .amount(new BigDecimal(1.1))
                .creditorBankBIC("EE1118")
                .creditorIban("EE9876")
                .currency(null)
                .debtorIban("DE1114")
                .paymentType(TYPE3)
                .details("type 3 details")
                .build();
        OkError result = paymentService.createPayment(request);
        verify(paymentRepository, never()).createNewPayment(any());

        assertThat(result).isNotNull();
        assertThat(result.getErrorMessage()).isEqualTo("invalid_currency");
        assertThat(result.getErrorCode()).isEqualTo("1003");
    }

    @Test
    void createPayment_invalidDebtorIban() {
        PaymentCreationRequest request = PaymentCreationRequest.builder()
                .amount(new BigDecimal(1.1))
                .creditorBankBIC("EE1237")
                .creditorIban("EE9875")
                .currency("EUR")
                .debtorIban(null)
                .paymentType(TYPE3)
                .details("type 3 ib")
                .build();
        OkError result = paymentService.createPayment(request);
        verify(paymentRepository, never()).createNewPayment(any());

        assertThat(result).isNotNull();
        assertThat(result.getErrorMessage()).isEqualTo("invalid_debtor_iban");
        assertThat(result.getErrorCode()).isEqualTo("1004");
    }

    @Test
    void createPayment_invalidCreditorIban() {
        PaymentCreationRequest request = PaymentCreationRequest.builder()
                .amount(new BigDecimal(1.1))
                .debtorIban("EE1236")
                .creditorIban(null)
                .currency("EUR")
                .creditorBankBIC("DE1112")
                .paymentType(TYPE3)
                .details("type 3 ib")
                .build();
        OkError result = paymentService.createPayment(request);
        verify(paymentRepository, never()).createNewPayment(any());

        assertThat(result).isNotNull();
        assertThat(result.getErrorMessage()).isEqualTo("invalid_creditor_iban");
        assertThat(result.getErrorCode()).isEqualTo("1005");
    }

    @Test
    void createPayment_invalidType1Currency() {
        PaymentCreationRequest request = PaymentCreationRequest.builder()
                .amount(new BigDecimal(1.1))
                .debtorIban("EE2")
                .creditorIban("EE1")
                .currency("USD")
                .creditorBankBIC("DE1")
                .paymentType(TYPE1)
                .details("details")
                .build();
        OkError result = paymentService.createPayment(request);
        verify(paymentRepository, never()).createNewPayment(any());

        assertThat(result).isNotNull();
        assertThat(result.getErrorMessage()).isEqualTo("invalid_currency");
        assertThat(result.getErrorCode()).isEqualTo("1100");
    }

    @Test
    void createPayment_invalidType1Details() {
        PaymentCreationRequest request = PaymentCreationRequest.builder()
                .amount(new BigDecimal(1.1))
                .debtorIban("EE2")
                .creditorIban("EE1")
                .currency("EUR")
                .paymentType(TYPE1)
                .details(null)
                .build();
        OkError result = paymentService.createPayment(request);
        verify(paymentRepository, never()).createNewPayment(any());

        assertThat(result).isNotNull();
        assertThat(result.getErrorMessage()).isEqualTo("invalid_details");
        assertThat(result.getErrorCode()).isEqualTo("1101");
    }

    @Test
    void createPayment_invalidType1BIC() {
        PaymentCreationRequest request = PaymentCreationRequest.builder()
                .amount(new BigDecimal(1.1))
                .debtorIban("EE2")
                .creditorIban("EE1")
                .currency("EUR")
                .creditorBankBIC("DE1")
                .paymentType(TYPE1)
                .details("details")
                .build();
        OkError result = paymentService.createPayment(request);
        verify(paymentRepository, never()).createNewPayment(any());

        assertThat(result).isNotNull();
        assertThat(result.getErrorMessage()).isEqualTo("bic_not_allowed");
        assertThat(result.getErrorCode()).isEqualTo("1102");
    }

    @Test
    void createPayment_invalidType2Currency() {
        PaymentCreationRequest request = PaymentCreationRequest.builder()
                .amount(new BigDecimal(1.1))
                .debtorIban("EE2")
                .creditorIban("EE1")
                .currency("EUR")
                .paymentType(TYPE2)
                .build();
        OkError result = paymentService.createPayment(request);
        verify(paymentRepository, never()).createNewPayment(any());

        assertThat(result).isNotNull();
        assertThat(result.getErrorMessage()).isEqualTo("invalid_currency");
        assertThat(result.getErrorCode()).isEqualTo("1200");
    }

    @Test
    void createPayment_invalidType2BIC() {
        PaymentCreationRequest request = PaymentCreationRequest.builder()
                .amount(new BigDecimal(1.1))
                .debtorIban("EE2")
                .creditorIban("EE1")
                .currency("USD")
                .creditorBankBIC("bicnotallowed")
                .paymentType(TYPE2)
                .build();
        OkError result = paymentService.createPayment(request);
        verify(paymentRepository, never()).createNewPayment(any());

        assertThat(result).isNotNull();
        assertThat(result.getErrorMessage()).isEqualTo("bic_not_allowed");
        assertThat(result.getErrorCode()).isEqualTo("1201");
    }

    @Test
    void createPayment_invalidType3Currency() {
        PaymentCreationRequest request = PaymentCreationRequest.builder()
                .amount(new BigDecimal(1.1))
                .debtorIban("EE2")
                .creditorIban("EE1")
                .currency("GBP")
                .creditorBankBIC("DE1")
                .paymentType(TYPE3)
                .creditorBankBIC("BIC")
                .details("details")
                .build();
        OkError result = paymentService.createPayment(request);
        verify(paymentRepository, never()).createNewPayment(any());

        assertThat(result).isNotNull();
        assertThat(result.getErrorMessage()).isEqualTo("invalid_currency");
        assertThat(result.getErrorCode()).isEqualTo("1300");
    }

    @Test
    void createPayment_invalidType3BIC() {
        PaymentCreationRequest request = PaymentCreationRequest.builder()
                .amount(new BigDecimal(1.1))
                .debtorIban("EE2")
                .creditorIban("EE1")
                .currency("EUR")
                .paymentType(TYPE3)
                .creditorBankBIC(null)
                .details("details")
                .build();
        OkError result = paymentService.createPayment(request);
        verify(paymentRepository, never()).createNewPayment(any());

        assertThat(result).isNotNull();
        assertThat(result.getErrorMessage()).isEqualTo("invalid_bic");
        assertThat(result.getErrorCode()).isEqualTo("1301");
    }

    @Test
    void cancelPayment_success() {
        Payment payment = Payment.builder()
                .id(1L)
                .createdAt(DateTime.now().minusHours(2))
                .paymentStatus(CREATED)
                .paymentType(TYPE1)
                .build();

        when(paymentRepository.findOneNotCancelled(any())).thenReturn(payment);

        paymentService.cancelPayment(payment.getId());
        long hours = Duration.between(payment.getCreatedAt(), DateTime.now()).toHours();

        assertThat(payment.getCancellationFee()).isEqualTo(TYPE1.coefficient.multiply(BigDecimal.valueOf(hours)));
        assertThat(payment.getPaymentStatus()).isEqualTo(CANCELLED);
        verify(paymentRepository).cancelPayment(payment);
    }

    @Test
    void cancelPayment_failCreatedDateNotToday() {
        Payment payment = Payment.builder()
                .id(1L)
                .createdAt(DateTime.now().minusHours(25))
                .paymentStatus(CREATED)
                .paymentType(TYPE1)
                .build();
        when(paymentRepository.findOneNotCancelled(any())).thenReturn(payment);
        verify(paymentRepository, never()).cancelPayment(payment);

        PaymentException exception = assertThrows(PaymentException.class, () -> paymentService.cancelPayment(payment.getId()));
        assertThat(exception.getMessage()).isEqualTo("Only not already cancelled payments that are created today can be cancelled!");
    }

    @Test
    void getCancelledPaymentById_success() {
        when(paymentRepository.findCancellationFeeById(any())).thenReturn(BigDecimal.valueOf(0.5));

        PaymentResponse payment = paymentService.getCancelledPaymentById(1L);

        assertThat(payment.getCancellationFee()).isNotNull();
        assertThat(payment.getPaymentId()).isNotNull();
    }

    @Test
    void getAllNotCancelledSortedPaymentIds_success() {
        paymentService.getAllNotCancelledSortedPaymentIds();
        verify(paymentRepository).findAllNotCancelledSortedByAmount();
    }
}
