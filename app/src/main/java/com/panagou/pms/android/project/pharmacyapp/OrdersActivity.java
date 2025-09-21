package com.panagou.pms.android.project.pharmacyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    private OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_orders);

        // Insets
        View root = findViewById(R.id.rootOrders);
        BottomNavigationView bottom = findViewById(R.id.bottom_nav);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, 0);
            if (bottom != null) {
                bottom.setPadding(bottom.getPaddingLeft(), bottom.getPaddingTop(),
                        bottom.getPaddingRight(), bars.bottom);
            }
            return insets;
        });

        RecyclerView rv = findViewById(R.id.rvOrders);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(orderId -> {});
        rv.setAdapter(adapter);

        if (bottom != null) {
            bottom.setSelectedItemId(R.id.nav_orders);
            bottom.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_products) {
                    Intent i = new Intent(this, ProductsActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);
                    return true;
                }
                if (id == R.id.nav_cart) {
                    Intent i = new Intent(this, CartActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);
                    return true;
                }
                if (id == R.id.nav_orders) return true;
                if (id == R.id.nav_profile) {
                    Intent i = new Intent(this, ProfileActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);
                    return true;
                }
                return false;
            });
        }

        loadOrders();
    }

    @Override protected void onResume() {
        super.onResume();
        BottomNavigationView bottom = findViewById(R.id.bottom_nav);
        if (bottom != null) bottom.setSelectedItemId(R.id.nav_orders);
    }

    private void loadOrders() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("orders")
                .whereEqualTo("userId", uid)  // <-- χωρίς orderBy
                .get()
                .addOnSuccessListener(snap -> {
                    List<OrderAdapter.OrderRow> rows = new ArrayList<>();
                    snap.getDocuments().forEach(doc -> {
                        Order o = doc.toObject(Order.class);
                        if (o == null) return;
                        OrderAdapter.OrderRow r = new OrderAdapter.OrderRow();
                        r.id = doc.getId();
                        r.total = (o.total == null) ? 0.0 : o.total;
                        r.status = o.status;
                        r.itemsCount = (o.items == null) ? 0 : o.items.size();
                        r.createdTime = o.createdAt;  // Date
                        rows.add(r);
                    });

                    // sort local by createdTime DESC
                    java.util.Collections.sort(rows, (a, b) -> {
                        java.util.Date da = a.createdTime;
                        java.util.Date db = b.createdTime;
                        if (da == null && db == null) return 0;
                        if (da == null) return 1;     // nulls last
                        if (db == null) return -1;
                        return db.compareTo(da);      // desc
                    });

                    adapter.setItems(rows);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Load failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
