package com.example.ProductService.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ProductService.Entity.Product;
import com.example.ProductService.Repository.ProductRepo;

@RestController
@RequestMapping("/products")
public class ProductController {
	
	
	@Autowired
	private ProductRepo prepo;
	
	
	@PostMapping
     public Product addSave(@RequestBody Product product) {
		
		return prepo.save(product);
	}
	
	@GetMapping
	public List<Product> getAllProducts(){
		
		return prepo.findAll();
	}
	
	
	@GetMapping("/{id}")
	public ResponseEntity<Product> getProductById(@PathVariable Long id){
		
		Product pro=prepo.findById(id).orElseThrow(()->new RuntimeException("Product not is found with this id"+id));
		return ResponseEntity.ok(pro);
	}
}
