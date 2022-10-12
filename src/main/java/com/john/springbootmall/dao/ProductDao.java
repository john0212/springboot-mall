package com.john.springbootmall.dao;

import com.john.springbootmall.model.Product;

public interface ProductDao {

    Product getProductById(Integer productId);
}
