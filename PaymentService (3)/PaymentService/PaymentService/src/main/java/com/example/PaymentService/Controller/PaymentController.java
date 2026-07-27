package com.example.PaymentService.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PaymentService.Dto.PaymentRequest;
import com.example.PaymentService.Dto.PaymentResponse;
import com.example.PaymentService.service.PaymentServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
	
	
	private final PaymentServiceImpl pservice;
	
	@PostMapping
	public ResponseEntity<PaymentResponse> processPayment(@RequestHeader("Idempotency-Key") String  IdempotencyKey ,@RequestBody PaymentRequest request){
		
		
		
		PaymentResponse response=pservice.processResponse(request, IdempotencyKey);
		
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/{paymentId}/refund")
	public ResponseEntity<PaymentResponse> refundPayment(
	        @PathVariable Long paymentId) {

	    PaymentResponse response =
	            pservice.refundPayment(paymentId);

	    return ResponseEntity.ok(response);
	}
	

}
