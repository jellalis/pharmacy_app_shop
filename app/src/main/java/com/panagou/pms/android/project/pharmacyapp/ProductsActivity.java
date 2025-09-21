package com.panagou.pms.android.project.pharmacyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
        setContentView(R.layout.activity_products); // root έχει id=root

        // 🔹 Insets ώστε να μην κρύβεται τίποτα κάτω από status/navigation bars
        final View root = findViewById(R.id.root);
        final View bottomNavView = findViewById(R.id.bottom_nav);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(v.getPaddingLeft(), bars.top, v.getPaddingRight(), v.getPaddingBottom());
                if (bottomNavView != null) {
                    bottomNavView.setPadding(
                            bottomNavView.getPaddingLeft(),
                            bottomNavView.getPaddingTop(),
                            bottomNavView.getPaddingRight(),
                            bars.bottom
                    );
                }
                return insets;
            });
        }

        // Bottom nav (χωρίς finish, με REORDER_TO_FRONT)
        BottomNavigationView bottom = findViewById(R.id.bottom_nav);
        if (bottom != null) {
            bottom.setSelectedItemId(R.id.nav_products);
            bottom.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_products) return true;
                if (id == R.id.nav_cart) {
                    Intent i = new Intent(this, CartActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);
                    return true;
                }
                if (id == R.id.nav_orders) {
                    Intent i = new Intent(this, OrdersActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);
                    return true;
                }
                if (id == R.id.nav_profile) {
                    Intent i = new Intent(this, ProfileActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);
                    return true;
                }
                return false;
            });
        }

        // RecyclerView + Adapter
        rv = findViewById(R.id.rvProducts);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter();
        rv.setAdapter(adapter);

        adapter.setOnProductClick(p -> {
            Intent i = new Intent(this, ProductDetailsActivity.class);
            i.putExtra("id", p.getId());
            i.putExtra("name", p.getName());
            i.putExtra("price", p.getPrice() == null ? 0.0 : p.getPrice());
            i.putExtra("description", p.getDescription());
            i.putExtra("imageUrl", p.getImageUrl());
            startActivity(i);
        });

        // Firestore
        db = FirebaseFirestore.getInstance();
        loadProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottom = findViewById(R.id.bottom_nav);
        if (bottom != null) bottom.setSelectedItemId(R.id.nav_products);
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
                            try { p.setId(doc.getId()); } catch (Exception ignore) {}
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
