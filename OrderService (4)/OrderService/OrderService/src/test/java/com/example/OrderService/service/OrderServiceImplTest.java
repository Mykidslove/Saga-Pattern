package com.example.OrderService.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.OrderService.Service.OrderServiceImpl;

import com.example.OrderService.repository.OrderRepo;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {
	
	
	@Mock
    private OrderRepo orderRepo;

    
   
    @Mock
    private WebClient.Builder webClientBuilder;
    
    @InjectMocks
    private OrderServiceImpl orderService;
    
    @Test
    void orderServiceShouldBeCreated() {

        assertNotNull(orderService);
    }
        
        @Test
        void placeOrder_success() {
        }

        @Test
        void placeOrder_existingIdempotencyKey() {
        }

        @Test
        void placeOrder_inventoryFailure() {
        }

        @Test
        void placeOrder_paymentFailure() {
        }

        @Test
        void placeOrder_refundAndReleaseCompensation() {
        }
}


