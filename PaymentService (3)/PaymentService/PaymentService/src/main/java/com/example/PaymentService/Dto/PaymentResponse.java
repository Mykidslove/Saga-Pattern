package com.example.PaymentService.Dto;

import java.math.BigDecimal;

import com.example.PaymentService.Entity.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
	
	private Long paymentId;

    private Long orderReference;

    private BigDecimal amount;

    private PaymentStatus status;

    private String idempotencyKey;

}
