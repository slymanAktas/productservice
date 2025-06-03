package com.saktas.productservice.controllers;

import com.saktas.productservice.dto.Coupon;
import com.saktas.productservice.models.Product;
import com.saktas.productservice.repositories.ProductRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Data
@RestController
@RequestMapping("productapi")
public class ProductRestController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${coupon.service.uri}")
    private String couponServiceUri;

    @PostMapping("/products")
    public Product create(@RequestBody Product product) {
        try {
            Coupon coupon = restTemplate.getForObject(couponServiceUri + "/" + product.getCouponCode(), Coupon.class);
            product.setPrice(product.getPrice().subtract(coupon.getDiscount()));
            System.out.println("-----> Coupon(" + coupon + ") Found <------");
        } catch (HttpClientErrorException err) {
            if (err.getMessage().contains("404")) {
                System.out.println("-----> Coupon Not Found <------");
            } else throw err;
        }

        return productRepository.save(product);
    }

    @GetMapping("/products/{productId}")
    public Optional<Product> getProduct(@PathVariable("productId") Long productId) {
        return productRepository.findById(productId);
    }
}
