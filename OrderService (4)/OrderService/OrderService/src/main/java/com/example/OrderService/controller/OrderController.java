package com.example.OrderService.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.example.OrderService.Entity.Order;
import com.example.OrderService.Service.OrderServiceImpl;



import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
	
	    private final OrderServiceImpl orderService;
	
	@PostMapping
	public ResponseEntity<Order> placeOrder(@RequestHeader("Idempotency-Key")String idempotencyKey,@RequestBody Order order
    ){
		
		//Order saveorder=orderService.placeOrder(order);

	
	    Order savedOrder =
	            orderService.placeOrder(
	                    order,
	                    idempotencyKey
	            );
	    
	    

	    return ResponseEntity.ok(savedOrder);
	}
	
	@GetMapping
	public ResponseEntity<Object> getAllOrders(){
		return ResponseEntity.ok(orderService.findAll());
	}

}
