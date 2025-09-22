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
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {

    private RecyclerView rv;
    private ProductAdapter adapter;
    private FirebaseFirestore db;
    private ListenerRegistration productsReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);


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

        // Bottom nav
        BottomNavigationView bottom = findViewById(R.id.bottom_nav);
        if (bottom != null) {
            bottom.setSelectedItemId(R.id.nav_products);
            bottom.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_products) return true;
                if (id == R.id.nav_cart) {
                    startActivity(new Intent(this, CartActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                    return true;
                }
                if (id == R.id.nav_orders) {
                    startActivity(new Intent(this, OrdersActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                    return true;
                }
                if (id == R.id.nav_profile) {
                    startActivity(new Intent(this, ProfileActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
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


        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false) // <-- no offline cache
                .build();
        db.setFirestoreSettings(settings);


        forceServerOnce();
    }

    @Override protected void onStart() {
        super.onStart();
        startProductsListener(); // ξεκινάμε real-time ακρόαση
    }

    @Override protected void onStop() {
        super.onStop();
        if (productsReg != null) { productsReg.remove(); productsReg = null; } // κλείσε listener
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottom = findViewById(R.id.bottom_nav);
        if (bottom != null) bottom.setSelectedItemId(R.id.nav_products);
    }


    private void forceServerOnce() {
        db.collection("products")
                .orderBy("name", Query.Direction.ASCENDING)
                .get(Source.SERVER)  // <-- bypass cache
                .addOnSuccessListener(snap -> {
                    List<Product> list = new ArrayList<>();
                    snap.getDocuments().forEach(doc -> {
                        Product p = doc.toObject(Product.class);
                        if (p != null) { p.setId(doc.getId()); list.add(p); }
                    });
                    adapter.setItems(list);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Server load error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }


    private void startProductsListener() {
        if (productsReg != null) { productsReg.remove(); productsReg = null; }

        productsReg = db.collection("products")
                .orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE, (snap, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Listen error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (snap == null) return;

                    boolean fromCache = snap.getMetadata().isFromCache();
                    android.util.Log.d("PRODUCTS", "snapshot fromCache=" + fromCache + " size=" + snap.size());

                    List<Product> list = new ArrayList<>();
                    snap.getDocuments().forEach(doc -> {
                        Product p = doc.toObject(Product.class);
                        if (p != null) { p.setId(doc.getId()); list.add(p); }
                    });
                    adapter.setItems(list);
                });
    }
}
