package com.john.springbootmall.constant;

public class MyTest {

    public static void main(String[] args) {
        ProductCategory category = ProductCategory.FOOD;
        String s = category.name(); // 把 enum 的值轉成 String 出來
        System.out.println(s); // FOOD

        String s2 = "CAR";
        ProductCategory category2 = ProductCategory.valueOf(s2);
    }
}
