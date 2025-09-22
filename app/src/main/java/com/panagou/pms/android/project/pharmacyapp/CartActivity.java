package com.panagou.pms.android.project.pharmacyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private CartAdapter adapter;
    private TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);


        View root = findViewById(R.id.rootCart);
        BottomNavigationView bottom = findViewById(R.id.bottom_nav);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(bars.left, bars.top, bars.right, 0);
                if (bottom != null) {
                    bottom.setPadding(
                            bottom.getPaddingLeft(),
                            bottom.getPaddingTop(),
                            bottom.getPaddingRight(),
                            bars.bottom
                    );
                }
                return insets;
            });
        }

        // RecyclerView + Total
        RecyclerView rv = findViewById(R.id.rvCart);
        tvTotal = findViewById(R.id.tvTotal);

        adapter = new CartAdapter(new CartAdapter.Listener() {
            @Override public void onIncrease(CartItem item) {
                CartRepo.get().setQuantity(item.getProduct(), item.getQuantity() + 1);
                refresh();
            }
            @Override public void onDecrease(CartItem item) {
                int q = Math.max(1, item.getQuantity() - 1);
                CartRepo.get().setQuantity(item.getProduct(), q);
                refresh();
            }
            @Override public void onRemove(CartItem item) {
                CartRepo.get().remove(item.getProduct());
                refresh();
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        // Checkout
        findViewById(R.id.btnCheckout).setOnClickListener(v -> checkout());

        // Bottom Navigation
        if (bottom != null) {
            bottom.setSelectedItemId(R.id.nav_cart);
            bottom.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_products) {
                    Intent i = new Intent(this, ProductsActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);
                    return true;
                }
                if (id == R.id.nav_cart) return true;
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

        refresh();
    }

    @Override protected void onResume() {
        super.onResume();
        BottomNavigationView bottom = findViewById(R.id.bottom_nav);
        if (bottom != null) bottom.setSelectedItemId(R.id.nav_cart);
        refresh();
    }

    private void refresh() {
        List<CartItem> now = new ArrayList<>(CartRepo.get().getItems());
        Log.d("CART", "refresh items=" + now.size());
        adapter.setItems(now);
        tvTotal.setText(String.format(Locale.getDefault(), "Total: €%.2f", CartRepo.get().total()));
    }

    private void checkout() {
        if (CartRepo.get().getItems().isEmpty()) {
            Toast.makeText(this, "Το καλάθι είναι άδειο", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Κάνε login για να ολοκληρώσεις την παραγγελία", Toast.LENGTH_LONG).show();
            return;
        }
        String uid = user.getUid();

        // Cart -> OrderItems
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem ci : CartRepo.get().getItems()) {
            Product p = ci.getProduct();
            String pid   = p.getId() == null ? "" : p.getId();
            String pname = p.getName();
            double price = (p.getPrice() == null) ? 0.0 : p.getPrice();
            int qty      = Math.max(1, ci.getQuantity());
            orderItems.add(new OrderItem(pid, pname, price, qty));
        }

        double total = CartRepo.get().total();
        Order order = new Order(uid, total, orderItems, "new");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        findViewById(R.id.btnCheckout).setEnabled(false);

        db.collection("orders").add(order)
                .addOnSuccessListener(doc -> {
                    Log.d("ORDERS", "Created: " + doc.getPath());
                    Toast.makeText(this, "Η παραγγελία καταχωρήθηκε", Toast.LENGTH_SHORT).show();
                    CartRepo.get().clear();
                    refresh();
                })
                .addOnFailureListener(e -> {
                    Log.e("ORDERS", "Create failed", e);
                    Toast.makeText(this, "Σφάλμα: " + e.getMessage(), Toast.LENGTH_LONG).show();
                })
                .addOnCompleteListener(t -> findViewById(R.id.btnCheckout).setEnabled(true));
    }
}
