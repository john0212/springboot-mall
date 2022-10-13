package com.john.springbootmall.dao;

import com.john.springbootmall.dto.ProductRequest;
import com.john.springbootmall.model.Product;

public interface ProductDao {

    Product getProductById(Integer productId);

    Integer createProduct(ProductRequest productRequest);
}
