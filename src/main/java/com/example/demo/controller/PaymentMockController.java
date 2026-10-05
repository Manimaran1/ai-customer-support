package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentMockController {

    @GetMapping("/api/payment/details")
    public String getPaymentDetails(@RequestParam String customerId,
                                    @RequestParam String transactionId) {
        return "Payment transaction " + transactionId + " for customer " + customerId +
                ": Declined due to insufficient funds.";
    }
}
