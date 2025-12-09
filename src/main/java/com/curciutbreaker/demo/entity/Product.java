package com.curciutbreaker.demo.entity;

import lombok.Data;

@Data
public class Product {
    String name;
    String description;
    String ean;
    Double price;
}
