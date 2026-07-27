package com.example.PaymentService.Dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
	
	
	private Long orderReference;

    private Long productId;

    private Integer quantity;

    private BigDecimal amount;

}
