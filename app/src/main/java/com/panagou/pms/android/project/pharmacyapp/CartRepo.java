package com.panagou.pms.android.project.pharmacyapp;

import java.util.ArrayList;
import java.util.List;

public class CartRepo {
    private static CartRepo INSTANCE;
    private final List<CartItem> items = new ArrayList<>();

    private CartRepo() {}
    public static synchronized CartRepo get() {
        if (INSTANCE == null) INSTANCE = new CartRepo();
        return INSTANCE;
    }

    public List<CartItem> getItems() { return items; }

    public void add(Product p, int qty) {
        android.util.Log.d("CART", "add: " +
                (p==null? "null" : (p.getName()+" | id="+p.getId()+" | price="+p.getPrice())) +
                " qty=" + qty);
        if (p == null || qty <= 0) return;
        for (CartItem ci : items) {
            if (same(p, ci.getProduct())) {
                ci.setQuantity(ci.getQuantity() + qty);
                return;
            }
        }
        items.add(new CartItem(p, qty));
    }


    public void remove(Product p) {
        items.removeIf(ci -> same(p, ci.getProduct()));
    }

    public void setQuantity(Product p, int qty) {
        for (CartItem ci : items) {
            if (same(p, ci.getProduct())) { ci.setQuantity(qty); return; }
        }
    }

    public double total() {
        double sum = 0;
        for (CartItem ci : items) sum += ci.getLineTotal();
        return sum;
    }

    public void clear(){
        items.clear();
    }
    private boolean same(Product a, Product b) {
        if (a == null || b == null) return false;
        if (a.getId() != null && b.getId() != null) return a.getId().equals(b.getId());

        return String.valueOf(a.getName()).equals(String.valueOf(b.getName()));
    }
}
