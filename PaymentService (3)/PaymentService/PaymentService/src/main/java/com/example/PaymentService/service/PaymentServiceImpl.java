package com.example.PaymentService.service;

import org.springframework.stereotype.Service;

import com.example.PaymentService.Dto.PaymentRequest;
import com.example.PaymentService.Dto.PaymentResponse;
import com.example.PaymentService.Entity.Payment;
import com.example.PaymentService.Entity.PaymentStatus;
import com.example.PaymentService.Repository.PaymentRepo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl {
	
	private final PaymentRepo prepo;
	
	
	
		@Transactional
		public PaymentResponse refundPayment(Long paymentId) {

		    Payment payment = prepo.findById(paymentId)
		            .orElseThrow(() ->
		                    new RuntimeException(
		                            "Payment not found: " + paymentId
		                    ));

		    if (payment.getStatus() == PaymentStatus.REFUNDED) {
		        return mapToResponse(payment);
		    }

		    if (payment.getStatus() != PaymentStatus.SUCCESS) {
		        throw new RuntimeException(
		                "Only successful payment can be refunded"
		        );
		    }

		    payment.setStatus(PaymentStatus.REFUNDED);

		    Payment refundedPayment = prepo.save(payment);

		    return mapToResponse(refundedPayment);
		
		}
	
	    @Transactional
		public PaymentResponse processResponse(PaymentRequest request, String idempotencyKey) {
	    	
	    	if (idempotencyKey == null || idempotencyKey.isBlank()) {
	    	    throw new RuntimeException(
	    	            "Idempotency key is required"
	    	    );
	    	}
			
	    	if (request == null) {
	    	    throw new RuntimeException(
	    	            "Payment request is required"
	    	    );
	    	}

	    	if (request.getProductId() == null) {
	    	    throw new RuntimeException(
	    	            "Product ID is required"
	    	    );
	    	}

	    	if (request.getQuantity() == null
	    	        || request.getQuantity() <= 0) {

	    	    throw new RuntimeException(
	    	            "Quantity must be greater than zero"
	    	    );
	    	}
          
	    	if (request.getAmount() == null
	    	        || request.getAmount().signum() <= 0) {

	    	    throw new RuntimeException(
	    	            "Payment amount must be greater than zero"
	    	    );
	    	}
		
		Payment existingpayment=prepo.findByIdempotencyKey(idempotencyKey).orElse(null);
		 if (existingpayment != null) {

	            boolean sameRequest =
	                    existingpayment.getProductId()
	                            .equals(request.getProductId())
	                    &&
	                    existingpayment.getQuantity()
	                            .equals(request.getQuantity())
	                    &&
	                    existingpayment.getAmount()
	                            .compareTo(request.getAmount()) == 0;
		 
		 
		 if (!sameRequest) {
             throw new RuntimeException(
                     "Idempotency key already used "
                     + "with different payment data"
             );
         }
		   return mapToResponse(existingpayment);
		 }
	
	 
         Payment payment = new Payment();

         payment.setOrderReference(request.getOrderReference());
         payment.setProductId(request.getProductId());
         payment.setQuantity(request.getQuantity());
         payment.setAmount(request.getAmount());
         payment.setStatus(PaymentStatus.SUCCESS);
         payment.setIdempotencyKey(idempotencyKey);

         Payment savedPayment = prepo.save(payment);

         return mapToResponse(savedPayment);
     
     }

	    
	   private PaymentResponse mapToResponse(Payment payment) {

	        return new PaymentResponse(payment.getId(),
	        		payment.getOrderReference(),
	        		payment.getAmount(),
	        		payment.getStatus(),
	        		payment.getIdempotencyKey()
	        		      		
	        );
	    }
	
	
	
}	



	