package com.panagou.pms.android.project.pharmacyapp;

public class OrderItem {
    public String productId;
    public String name;
    public Double price;
    public int qty;

    public OrderItem() {}
    public OrderItem(String productId, String name, Double price, int qty) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.qty = qty;
    }
}