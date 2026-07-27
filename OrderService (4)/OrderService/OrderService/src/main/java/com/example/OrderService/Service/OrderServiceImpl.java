
package com.example.OrderService.Service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.OrderService.Entity.Order;
import com.example.OrderService.Entity.OrderStatus;
import com.example.OrderService.Entity.PaymentStatus;
import com.example.OrderService.dto.InventoryDto;
import com.example.OrderService.dto.PaymentRequest;
import com.example.OrderService.dto.PaymentResponse;
import com.example.OrderService.dto.Product;
import com.example.OrderService.repository.OrderRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl {

    private final OrderRepo orderRepo;
    private final WebClient.Builder webClientBuilder;

    public Order placeOrder(Order order, String idempotencyKey) {

        Order existingOrder = orderRepo
                .findByIdempotencyKey(idempotencyKey)
                .orElse(null);

        if (existingOrder != null) {
            return existingOrder;
        }

        Product product = getProduct(order.getProductId());

        BigDecimal totalAmount =
                BigDecimal.valueOf(product.getPrice())
                        .multiply(BigDecimal.valueOf(order.getQuantity()));

        order.setAmount(totalAmount);
        order.setIdempotencyKey(idempotencyKey);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        Order savedOrder = orderRepo.save(order);

        boolean inventoryReserved = false;
        boolean paymentCompleted = false;

        try {

            reserveInventory(
                    savedOrder.getProductId(),
                    savedOrder.getQuantity(),
                    savedOrder.getId()
            );

            inventoryReserved = true;

            PaymentRequest paymentRequest = new PaymentRequest(
                    savedOrder.getId(),
                    savedOrder.getProductId(),
                    savedOrder.getQuantity(),
                    totalAmount
            );

            PaymentResponse payment =
                    processPayment(paymentRequest, idempotencyKey);

            if (payment == null
                    || payment.getStatus() != PaymentStatus.SUCCESS) {

                throw new RuntimeException("Payment failed");
            }

            paymentCompleted = true;

            savedOrder.setPaymentId(payment.getPaymentId());
            savedOrder.setPaymentStatus(PaymentStatus.SUCCESS);

            // Temporary test condition
            if (savedOrder.getQuantity() == 4) {
                throw new RuntimeException(
                        "Simulated order confirmation failure"
                );
            }

            savedOrder.setOrderStatus(OrderStatus.CONFIRMED);

            return orderRepo.save(savedOrder);

        } catch (Exception ex) {

            log.error(
                    "Saga failed for orderId={}, reason={}",
                    savedOrder.getId(),
                    ex.getMessage()
            );

            if (paymentCompleted&& savedOrder.getPaymentId() != null) {
                refundPayment(
                        savedOrder.getPaymentId(),
                        idempotencyKey
                );
                
                savedOrder.setPaymentStatus(
                        PaymentStatus.REFUNDED
                );

            } else {

                savedOrder.setPaymentStatus(
                        PaymentStatus.FAILED
                );
            }
            }

            if (inventoryReserved) {
                releaseInventory(
                        savedOrder.getProductId(),
                        savedOrder.getQuantity(),
                        savedOrder.getId()
                );
            }

            savedOrder.setOrderStatus(OrderStatus.CANCELLED);


            return orderRepo.save(savedOrder);
        }
    

    private Product getProduct(Long productId) {

        Product product = webClientBuilder.build()
                .get()
                .uri("http://PRODUCTSERVICE/products/" + productId)
                .retrieve()
                .bodyToMono(Product.class)
                .block();

        if (product == null) {
            throw new RuntimeException("Product not found");
        }

        return product;
    }

    private void reserveInventory(
            Long productId,
            Integer quantity,
            Long orderId) {

        InventoryDto response = webClientBuilder.build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("INVENTORYSERVICE")
                        .path("/inventory/reserve")
                        .queryParam("productId", productId)
                        .queryParam("quantity", quantity)
                        .queryParam("orderId", orderId)
                        .build())
                .retrieve()
                .bodyToMono(InventoryDto.class)
                .block();

        if (response == null || !response.isInstock()) {
            throw new RuntimeException("Inventory reservation failed");
        }
    }

    private PaymentResponse processPayment(
            PaymentRequest paymentRequest,
            String idempotencyKey) {

        return webClientBuilder.build()
                .post()
                .uri("http://PAYMENTSERVICE/payments")
                .header("Idempotency-Key", idempotencyKey)
                .bodyValue(paymentRequest)
                .retrieve()
                .bodyToMono(PaymentResponse.class)
                .block();
    }

    private void releaseInventory(
            Long productId,
            Integer quantity,
            Long orderId) {

        try {
            webClientBuilder.build()
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("http")
                            .host("INVENTORYSERVICE")
                            .path("/inventory/release")
                            .queryParam("productId", productId)
                            .queryParam("quantity", quantity)
                            .queryParam("orderId", orderId)
                            .build())
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            log.info(
                    "Inventory released for orderId={}",
                    orderId
            );

        } catch (Exception ex) {

            log.error(
                    "Inventory compensation failed for orderId={}, reason={}",
                    orderId,
                    ex.getMessage()
            );
        }
    }

    private void refundPayment(
            Long paymentId,
            String idempotencyKey) {

        try {
            webClientBuilder.build()
                    .post()
                    .uri("http://PAYMENTSERVICE/payments/"
                            + paymentId + "/refund")
                    .header(
                            "Idempotency-Key",
                            "refund-" + idempotencyKey
                    )
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            log.info(
                    "Payment refunded. paymentId={}",
                    paymentId
            );

        } catch (Exception ex) {

            log.error(
                    "Payment compensation failed. paymentId={}, reason={}",
                    paymentId,
                    ex.getMessage()
            );
        }
    }

    public List<Order> findAll() {
        return orderRepo.findAll();
    }
}