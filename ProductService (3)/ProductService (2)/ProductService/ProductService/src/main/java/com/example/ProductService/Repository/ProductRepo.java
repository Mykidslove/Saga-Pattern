package com.example.ProductService.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ProductService.Entity.Product;


@Repository
public interface ProductRepo extends JpaRepository<Product,Long> {

}
