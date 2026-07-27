package com.example.InventoryService.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.InventoryService.Dto.InventoryDto;
import com.example.InventoryService.Entity.Inventory;
import com.example.InventoryService.Service.InventoryServiceimpl;



@RestController
@RequestMapping("/inventory")
public class InventoryController {
	
	
	@Autowired
	private InventoryServiceimpl Iservice;
	
	
	@PostMapping
	public ResponseEntity<Inventory> addInventory(@RequestBody Inventory inven){
		
		return ResponseEntity.ok(Iservice.saveInventory(inven));
	}
	

	@GetMapping("/check")
	public ResponseEntity<InventoryDto> checkStock(@RequestParam Long productId, @RequestParam Integer quantity){
		
		return ResponseEntity.ok(Iservice.checkStock(productId,quantity));
		
	}
	
	@PostMapping("/reserve")
	public ResponseEntity<InventoryDto> reserveInventory(
	        @RequestParam Long productId,
	        @RequestParam Integer quantity,
	        @RequestParam Long orderId) {

	    InventoryDto response =
	            Iservice.reserveInventory(
	                    productId,
	                    quantity,
	                    orderId
	            );

	    return ResponseEntity.ok(response);
	}
	@PostMapping("/release")
	public ResponseEntity<InventoryDto> releaseInventory(
	        @RequestParam Long productId,
	        @RequestParam Integer quantity,
	        @RequestParam Long orderId) {

	    InventoryDto response =
	            Iservice.releaseInventory(
	                    productId,
	                    quantity,
	                    orderId
	            );

	    return ResponseEntity.ok(response);
	}
	
	
}
