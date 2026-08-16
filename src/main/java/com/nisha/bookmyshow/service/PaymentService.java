package com.nisha.bookmyshow.service;

import com.nisha.bookmyshow.entity.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Simulated payment gateway. A real integration would call Razorpay/Stripe here.
 * This implementation always returns SUCCESS so the booking flow can be tested end-to-end.
 */
@Service
@Slf4j
public class PaymentService {

    public PaymentStatus charge(String bookingReference, BigDecimal amount) {
        log.info("Simulating payment of {} for {}", amount, bookingReference);
        return PaymentStatus.SUCCESS;
    }
}
