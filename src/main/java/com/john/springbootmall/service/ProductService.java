package com.john.springbootmall.service;

import com.john.springbootmall.dto.ProductRequest;
import com.john.springbootmall.model.Product;

public interface ProductService {

    Product getProductById(Integer productId);

    Integer createProduct(ProductRequest productRequest);
}
