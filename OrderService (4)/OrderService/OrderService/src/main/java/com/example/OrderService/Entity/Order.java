package com.example.OrderService.Entity;


import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="Orders")
public class Order {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long productId;
	private Integer quantity;
	
//	@Column(name = "idempotency_key", nullable = false, unique = true)
//    private String idempotencyKey;
	
	
	private Long paymentId;

	
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
	private PaymentStatus paymentStatus;
	
	 @Column(name = "idempotency_key", unique = true)
	    private String idempotencyKey;
	 
	 
	    @Enumerated(EnumType.STRING)
	    @Column(length = 20)
	 private OrderStatus orderStatus;
	    
	    private BigDecimal amount;

}
