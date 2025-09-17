package com.panagou.pms.android.project.pharmacyapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {

    private RecyclerView rv;
    private ProductAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products); // <-- νέο layout

        // Bottom nav (ίδιο όπως πριν)
        BottomNavigationView bottom = findViewById(R.id.bottom_nav);
        bottom.setSelectedItemId(R.id.nav_products);
        bottom.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_products) return true;
            if (id == R.id.nav_cart)    { startActivity(new Intent(this, CartActivity.class)); finish(); return true; }
            if (id == R.id.nav_orders)  { startActivity(new Intent(this, OrdersActivity.class)); finish(); return true; }
            if (id == R.id.nav_profile) { startActivity(new Intent(this, ProfileActivity.class)); finish(); return true; }
            return false;
        });

        // RecyclerView
        rv = findViewById(R.id.rvProducts);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter();
        rv.setAdapter(adapter);
        adapter.setOnProductClick(p -> {
            Intent i = new Intent(this, ProductDetailsActivity.class);
            i.putExtra("id", p.getId());
            i.putExtra("name", p.getName());
            i.putExtra("price", p.getPrice()==null?0.0:p.getPrice());
            i.putExtra("description", p.getDescription());
            i.putExtra("imageUrl", p.getImageUrl());

            startActivity(i);
        });


        // Firestore
        db = FirebaseFirestore.getInstance();

        loadProducts();
    }

    private void loadProducts() {
        db.collection("products")
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(snap -> {
                    List<Product> list = new ArrayList<>();
                    snap.forEach(doc -> {
                        Product p = doc.toObject(Product.class);
                        if (p != null) {
                            p.setId(doc.getId());   // 👈 εδώ αποθηκεύουμε το ID
                            list.add(p);
                        }
                    });
                    adapter.setItems(list);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Load error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

}
