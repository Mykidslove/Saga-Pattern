package com.example.PaymentService.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.PaymentService.Entity.Payment;

public interface PaymentRepo extends JpaRepository<Payment,Long> {

	Optional<Payment> findByIdempotencyKey(String idempotencyKey);

}
