package com.example.InventoryService.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryDto {
	
	
	 private Long productId;

	    

	    private Integer availableQuantity;
	    
	    private boolean instock;

}
