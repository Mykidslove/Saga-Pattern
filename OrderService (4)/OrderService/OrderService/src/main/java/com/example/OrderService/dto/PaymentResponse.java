package com.example.OrderService.dto;

import java.math.BigDecimal;

import com.example.OrderService.Entity.*;

import lombok.Data;

@Data
public class PaymentResponse {
	
	private Long paymentId;

    private Long orderReference;

    private BigDecimal amount;

    private PaymentStatus status;

    private String idempotencyKey;

}
