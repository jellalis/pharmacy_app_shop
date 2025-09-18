package com.panagou.pms.android.project.pharmacyapp;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
import java.util.List;

public class Order {
    public String userId;
    public Double total;
    public List<OrderItem> items;
    public String status; // e.g. "new"
    @ServerTimestamp public Date createdAt;

    public Order() {}
    public Order(String userId, Double total, List<OrderItem> items, String status) {
        this.userId = userId;
        this.total = total;
        this.items = items;
        this.status = status;
    }
}