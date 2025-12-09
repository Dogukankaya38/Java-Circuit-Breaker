package com.curciutbreaker.demo.controller;

import com.curciutbreaker.demo.entity.Product;
import com.curciutbreaker.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{code}")
    public Product getProduct(@PathVariable String code) {
        return productService.getProduct(code);
    }
}