package com.example.OrderService.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.OrderService.Entity.Order;

@Repository
public interface OrderRepo extends JpaRepository<Order,Long> {


	Optional<Order> findByIdempotencyKey(String idempotencyKey);
}
