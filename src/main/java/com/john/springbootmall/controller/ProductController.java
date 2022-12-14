package com.john.springbootmall.controller;

import com.john.springbootmall.constant.ProductCategory;
import com.john.springbootmall.dto.ProductQueryParams;
import com.john.springbootmall.dto.ProductRequest;
import com.john.springbootmall.model.Product;
import com.john.springbootmall.service.ProductService;
import com.john.springbootmall.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    // 當我們在實作列表(List)類型的 api 時，不論有沒有查到數據，那都要固定去返回 200 OK 的 http 狀態碼給前端
    // 如果視作查詢單個數據的 api ，則是有查到數據才回 200 OK ，沒查到就要返回 404 Not Found

    // 查詢整個商品列表
    @GetMapping("/products")
    public ResponseEntity<Page<Product>> getProducts(
            // 加上 required = false 代表說不一定要添加 category 這個參數，沒加這行的話就會導致沒有添加 category 這個參數就會產生錯誤
            // 如果前端沒有傳遞 category 這個參數過來時，這個值就是 null

            // 查詢條件 Filtering
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) String search,

            // 排序 Sorting
            @RequestParam(defaultValue = "created_date") String orderBy, // 就是要去根據甚麼樣的欄位進行排序
            @RequestParam(defaultValue = "desc") String sort,     // 就是要去決定我們要從小排到大，還是大排到小

            // 分頁 pagination
            @RequestParam(defaultValue = "5") @Max(1000) @Min(0) Integer limit,  // 這個參數是在說這次要取得幾筆數據
            @RequestParam(defaultValue = "0") @Min(0) Integer offset  // 這個參數是在說我們要跳過多少筆數據
    ){
        // 會把前端傳過來的參數，統一的整理到 ProductQueryParams 的變數裡面，然後再將這個變數，丟到 getProducts() 裡做傳遞
        // 這樣做的好處，是以後如果還想添加新的查詢條件時，就不需要在像以前一樣，去修改 Service 層跟 Dao 層的方法定義
        // 就只要在 ProductQueryParams 的 class 裡面再去新增一個變數，在一起把這個查詢條件給傳過去就好了
        ProductQueryParams productQueryParams = new ProductQueryParams();
        productQueryParams.setCategory(category);
        productQueryParams.setSearch(search);
        productQueryParams.setOrderBy(orderBy);
        productQueryParams.setSort(sort);
        productQueryParams.setLimit(limit);
        productQueryParams.setOffset(offset);

        // 取得 product list
        List<Product> productList = productService.getProducts(productQueryParams);

        // 取得 product 總數
        // 這個方法是在表示說他會去根據傳進去的 productQueryParams 參數，
        // 根據這些查詢條件，去計算出商品的總數有多少筆了
        // 之所以要傳 productQueryParams 的參數進去，是因為在不同查詢條件下，查出來的商品總筆數會不同
        Integer total = productService.countProduct(productQueryParams);

        // 分頁
        Page<Product> page = new Page<>();
        page.setLimit(limit);
        page.setOffset(offset);
        page.setTotal(total);
        page.setResult(productList);

        return ResponseEntity.status(HttpStatus.OK).body(page);
    }

    // 查詢某一個特定 id 的商品
    @GetMapping("/products/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable Integer productId) {
        Product product = productService.getProductById(productId);

//        System.out.println(product.getCreatedDate());

        if (product != null) {
            return ResponseEntity.status(HttpStatus.OK).body(product);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductRequest productRequest) {
        Integer productId = productService.createProduct(productRequest);

        Product product = productService.getProductById(productId);

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer productId,
                                                 @RequestBody @Valid ProductRequest productRequest) {

        // 先去檢查 product 是否存在
        Product product = productService.getProductById(productId);

        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            // 修改商品的數據
            productService.updateProduct(productId, productRequest);

            // 取得更新後的商品數據
            Product updatedProduct = productService.getProductById(productId);

            return ResponseEntity.status(HttpStatus.OK).body(updatedProduct);

        }
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer productId){
        // 不用像修改商品一樣要先去查看有沒有這個 id ，因為最終就是要去刪掉這項商品，不用去管說他是否真的存在

        productService.deleteProductById(productId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

