package com.example.PaymentService.Entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.UniqueConstraint;
@Entity
@Table(name="payments",
uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_payment_idempotency_key",
            columnNames = "idempotency_key"
        )
    }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

    private Long orderReference;

    private Long productId;

    private Integer quantity;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(
            name = "idempotency_key",
            nullable = false,
            unique = true
        )
    private String idempotencyKey;

}
