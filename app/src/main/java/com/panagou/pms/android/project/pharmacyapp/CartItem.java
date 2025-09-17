package com.panagou.pms.android.project.pharmacyapp;

public class CartItem {
    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = Math.max(1, quantity);
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int q) { this.quantity = Math.max(1, q); }

    public double getLineTotal() {
        Double p = (product.getPrice() == null) ? 0.0 : product.getPrice();
        return p * quantity;
    }
}
