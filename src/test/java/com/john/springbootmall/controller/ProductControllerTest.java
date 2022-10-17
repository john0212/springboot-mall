package com.john.springbootmall.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.john.springbootmall.constant.ProductCategory;
import com.john.springbootmall.dto.ProductRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    // 查詢商品
    @Test
    public void getProduct_success() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/products/{productId}",1);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName", equalTo("蘋果（澳洲）")))
                .andExpect(jsonPath("$.category", equalTo("FOOD")))
                .andExpect(jsonPath("$.imageUrl", notNullValue()))
                .andExpect(jsonPath("$.price", notNullValue()))
                .andExpect(jsonPath("$.stock", notNullValue()))
                .andExpect(jsonPath("$.description", notNullValue()))
                .andExpect(jsonPath("$.createdDate", notNullValue()))
                .andExpect(jsonPath("$.lastModifiedDate", notNullValue()));

    }

    @Test
    public void getProduct_notFound() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/products/{productId}",20000);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(404));
    }

    // 創建商品
    @Test
    @Transactional
    public void createProduct_success() throws Exception {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductName("test food product");
        productRequest.setCategory(ProductCategory.FOOD);
        productRequest.setImageUrl("http://test.com");
        productRequest.setPrice(100);
        productRequest.setStock(2);

        String json = objectMapper.writeValueAsString(productRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.productName", equalTo("test food product")))
                .andExpect(jsonPath("$.category", equalTo("FOOD")))
                .andExpect(jsonPath("$.imageUrl", equalTo("http://test.com")))
                .andExpect(jsonPath("$.price", equalTo(100)))
                .andExpect(jsonPath("$.stock", equalTo(2)))
                .andExpect(jsonPath("$.description", nullValue()))
                .andExpect(jsonPath("$.createdDate", notNullValue()))
                .andExpect(jsonPath("$.lastModifiedDate", notNullValue()));

    }

    // 測試前端請求參數帶錯的情況
    @Test
    @Transactional
    public void createProduct_illegalArgument() throws Exception {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductName("test food product");

        String json = objectMapper.writeValueAsString(productRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    // 更新商品
    @Test
    @Transactional
    public void updateProduct_success() throws Exception {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductName("test food product");
        productRequest.setCategory(ProductCategory.FOOD);
        productRequest.setImageUrl("http://test.com");
        productRequest.setPrice(100);
        productRequest.setStock(2);

        String json = objectMapper.writeValueAsString(productRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/products/{productId}",3)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.productName", equalTo("test food product")))
                .andExpect(jsonPath("$.category", equalTo("FOOD")))
                .andExpect(jsonPath("$.imageUrl", equalTo("http://test.com")))
                .andExpect(jsonPath("$.price", equalTo(100)))
                .andExpect(jsonPath("$.stock", equalTo(2)))
                .andExpect(jsonPath("$.description", nullValue()))
                .andExpect(jsonPath("$.createdDate", notNullValue()))
                .andExpect(jsonPath("$.lastModifiedDate", notNullValue()));
    }

    @Test
    @Transactional
    public void updateProduct_illegalArgument() throws Exception {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductName("test food product");

        String json = objectMapper.writeValueAsString(productRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/products/{productId}",2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    @Test
    @Transactional
    public void updateProduct_productNotFound() throws Exception {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductName("test food product");
        productRequest.setCategory(ProductCategory.FOOD);
        productRequest.setImageUrl("http://test.com");
        productRequest.setPrice(100);
        productRequest.setStock(2);


        String json = objectMapper.writeValueAsString(productRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/products/{productId}",20000)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(404));
    }

    // 刪除商品
    @Test
    @Transactional
    public void deleteProduct_success() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/products/{productId}",4);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(204));
    }

    @Test
    @Transactional
    public void deleteProduct_illegalArgument() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/products/{productId}",20000);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(204));
    }

    // 查詢商品列表
    // 測試不帶任何參數情況下，是否能取得商品數據。ex:會去測試查詢商品關鍵字、或測試查詢商品分類這些參數
    @Test
    public void getProducts() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/products");

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit",notNullValue()))
                .andExpect(jsonPath("$.offset",notNullValue()))
                .andExpect(jsonPath("$.total",notNullValue()))
                .andExpect(jsonPath("$.result",hasSize(5)));
    }

    // 主要去測試查詢條件相關的參數
    // 下面的例子就是說 關鍵字 B 且類型為 CAR 的總類有 2 個
    @Test
    public void getProducts_filtering() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/products")
                .param("search","B")
                .param("category","CAR");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit",notNullValue()))
                .andExpect(jsonPath("$.offset",notNullValue()))
                .andExpect(jsonPath("$.total",notNullValue()))
                .andExpect(jsonPath("$.result",hasSize(2)));
    }

    // 測試排序相關的參數
    @Test
    public void getProducts_sorting() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/products")
                .param("orderBy","price")
                .param("sort","desc");

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit",notNullValue()))
                .andExpect(jsonPath("$.offset",notNullValue()))
                .andExpect(jsonPath("$.total",notNullValue()))
                .andExpect(jsonPath("$.result",hasSize(5)))
                .andExpect(jsonPath("$.result[0].productId", equalTo(6)))
                .andExpect(jsonPath("$.result[1].productId", equalTo(5)))
                .andExpect(jsonPath("$.result[2].productId", equalTo(7)))
                .andExpect(jsonPath("$.result[3].productId", equalTo(4)))
                .andExpect(jsonPath("$.result[4].productId", equalTo(2)));
    }

    // 測試分頁相關的參數
    @Test
    public void getProducts_pagination() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/products")
                .param("limit","2")
                .param("offset","2");

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit",notNullValue()))
                .andExpect(jsonPath("$.offset",notNullValue()))
                .andExpect(jsonPath("$.total",notNullValue()))
                .andExpect(jsonPath("$.result",hasSize(2)))
                .andExpect(jsonPath("$.result[0].productId", equalTo(5)))
                .andExpect(jsonPath("$.result[1].productId", equalTo(4)));
    }

}