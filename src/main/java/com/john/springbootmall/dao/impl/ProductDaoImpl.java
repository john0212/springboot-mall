package com.john.springbootmall.dao.impl;

import com.john.springbootmall.dao.ProductDao;
import com.john.springbootmall.dto.ProductQueryParams;
import com.john.springbootmall.dto.ProductRequest;
import com.john.springbootmall.model.Product;
import com.john.springbootmall.rowmapper.ProductRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProductDaoImpl implements ProductDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Product> getProducts(ProductQueryParams productQueryParams) {

        String sql = "SELECT product_id,product_name, category, image_url, price, stock, description, " +
                "created_date, last_modified_date " +
                "FROM product WHERE 1=1";
        // 加上 WHERE 1=1 最主要的理由是想要讓下面的查詢條件可以去自由的拚接在這個 sql 語法後面，WHERE 1=1 本身對查詢結果不會有任何影響
        // 在使用 WHERE 1=1 時有一個很重要的重點，就是我們在寫這個拼接的 AND 的 sql 語句的時候，一定要在 AND 的前面，去預留一個空白鍵，這樣才不會跟前面的查詢條件黏在一起

        Map<String, Object> map = new HashMap<>();

        // 查詢條件
        if (productQueryParams.getCategory() != null) {
            sql = sql + " AND category = :category";
            // 因為 category 的類型是 Enum 類型，所以我們要使用 .name() 將這個 Enum 類型去轉換成是一個字串，
            // 然後才把這個字串的值，去加到這個 map 裡面，這裡要特別注意
            map.put("category", productQueryParams.getCategory().name());
        }

        // LIKE 會搭配 % 一起使用
        // %search -> 以甚麼 search 為結尾的數據
        // search% -> 以 search 為開頭的數據
        // %search% -> 只要有 search 這個字的都會找出來
        if (productQueryParams.getSearch() != null) {
            sql = sql + " AND product_name LIKE :search";
            map.put("search", "%" + productQueryParams.getSearch() + "%");
        }

        // 排序
        // 因為在前面有設定就算沒有填也有預設值，所以這邊不需要去判斷是否為 null
        sql = sql + " ORDER BY " + productQueryParams.getOrderBy() + " " + productQueryParams.getSort();

        // 分頁
        sql = sql + " LIMIT :limit OFFSET :offset";
        map.put("limit",productQueryParams.getLimit());
        map.put("offset",productQueryParams.getOffset());

        List<Product> productList = namedParameterJdbcTemplate.query(sql, map, new ProductRowMapper());

        return productList;
    }

    @Override
    public Product getProductById(Integer productId) {

        String sql = "SELECT product_id,product_name, category, image_url, price, stock, description, " +
                "created_date, last_modified_date " +
                "FROM product WHERE product_id = :productId";

        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);

        List<Product> productList = namedParameterJdbcTemplate.query(sql, map, new ProductRowMapper());

        if (productList.size() > 0) {
            return productList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Integer createProduct(ProductRequest productRequest) {

        String sql = "INSERT INTO product (product_name, category, image_url, price, stock," +
                " description, created_date, last_modified_date) " +
                "VALUES (:productName, :category, :imageUrl, :price, :stock," +
                ":description, :createdDate, :lastModifiedDate)";

        Map<String, Object> map = new HashMap<>();
        map.put("productName", productRequest.getProductName());
        map.put("category", productRequest.getCategory().toString());
        map.put("imageUrl", productRequest.getImageUrl());
        map.put("price", productRequest.getPrice());
        map.put("stock", productRequest.getStock());
        map.put("description", productRequest.getDescription());

        Date now = new Date();
        map.put("createdDate", now);
        map.put("lastModifiedDate", now);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        int productId = keyHolder.getKey().intValue();

        return productId;
    }

    @Override
    public void updateProduct(Integer productId, ProductRequest productRequest) {

        String sql = "UPDATE product SET product_name = :productName, category = :category," +
                "image_url = :imageUrl, price = :price, stock = :stock," +
                "description = :description, last_modified_date = :lastModifiedDate" +
                " WHERE product_id = :productId ";

        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);

        map.put("productName", productRequest.getProductName());
        map.put("category", productRequest.getCategory().toString());
        map.put("imageUrl", productRequest.getImageUrl());
        map.put("price", productRequest.getPrice());
        map.put("stock", productRequest.getStock());
        map.put("description", productRequest.getDescription());

        map.put("lastModifiedDate", new Date());

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void deleteProductById(Integer productId) {

        String sql = "DELETE FROM product WHERE product_id = :productId";

        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);

        namedParameterJdbcTemplate.update(sql, map);
    }
}
