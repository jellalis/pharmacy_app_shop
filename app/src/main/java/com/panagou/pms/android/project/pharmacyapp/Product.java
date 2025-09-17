package com.panagou.pms.android.project.pharmacyapp;

public class Product {
    private String id;   // 🔑 Firestore document ID
    private String name;
    private Double price;
    private String description;
    private String imageUrl;

    public Product() {} // Firestore θέλει empty constructor

    public Product(String id, String name, Double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public Double getPrice() { return price; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }

    // Setters (αν τα χρειαστείς)
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(Double price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
