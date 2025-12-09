package com.curciutbreaker.demo.service;

import com.curciutbreaker.demo.entity.Product;
import com.curciutbreaker.demo.executor.ResilientExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ResilientExecutor executor;
    private final Random random = new Random();

    public Product getProduct(String code) {

        return executor.execute(
                "productService",
                () -> callRemoteApi(code),
                this::fallbackProduct
        );
    }

    public Product callRemoteApi(String code) {

        if ("ERR".equalsIgnoreCase(code)) {
            throw new RuntimeException("Zorla hata üretildi!");
        }

        Product p = new Product();
        p.setName("Product-" + code);
        p.setDescription("Auto generated product for code " + code);

        p.setEan(String.valueOf(1000000000000L + random.nextInt(900000000)));

        double price = 50 + (500 - 50) * random.nextDouble();
        p.setPrice(Math.round(price * 100.0) / 100.0);

        return p;
    }


    private Product fallbackProduct() {
        Product p = new Product();
        p.setName("Fallback Product");
        p.setDescription("Ürün alınamadı, fallback döndü.");
        p.setEan("0000000000000");
        p.setPrice(0.0);
        return p;
    }
}
