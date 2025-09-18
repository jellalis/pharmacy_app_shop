package com.panagou.pms.android.project.pharmacyapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {
    private CartAdapter adapter;
    private TextView tvTotal;

    @Override protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

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
        findViewById(R.id.btnCheckout).setOnClickListener(v -> checkout());

        // padding για να μην "τρώγεται" από bottom nav
        rv.setClipToPadding(false);
        int pad = (int)(16 * getResources().getDisplayMetrics().density);
        rv.setPadding(0, 0, 0, pad);

        refresh();
    }

    private void refresh() {
        adapter.setItems(new ArrayList<>(CartRepo.get().getItems()));
        tvTotal.setText(String.format(Locale.getDefault(), "Total: €%.2f", CartRepo.get().total()));
    }

    private void checkout() {
        if (CartRepo.get().getItems().isEmpty()) {
            android.widget.Toast.makeText(this, "Το καλάθι είναι άδειο", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        // 1) Φτιάχνουμε OrderItems από τα CartItems
        java.util.List<OrderItem> orderItems = new java.util.ArrayList<>();
        for (CartItem ci : CartRepo.get().getItems()) {
            Product p = ci.getProduct();
            String pid = p.getId() == null ? "" : p.getId(); // αν πρόσθεσες id στο Product
            double price = p.getPrice() == null ? 0.0 : p.getPrice();
            int qty = Math.max(1, ci.getQuantity());
            orderItems.add(new OrderItem(pid, p.getName(), price, qty));
        }

        double total = CartRepo.get().total();
        String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null
                ? com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "guest";

        Order order = new Order(uid, total, orderItems, "new");

        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        db.collection("orders")
                .add(order)
                .addOnSuccessListener(docRef -> {
                    android.widget.Toast.makeText(this, "Η παραγγελία καταχωρήθηκε", android.widget.Toast.LENGTH_SHORT).show();
                    // 2) Καθάρισε το καλάθι και ανανέωσε UI
                    CartRepo.get().clear();
                    refresh();
                    // (προαιρετικό) πήγαινέ τον σε OrdersActivity
                    // startActivity(new Intent(this, OrdersActivity.class));
                    // finish();
                })
                .addOnFailureListener(e -> {
                    android.widget.Toast.makeText(this, "Σφάλμα: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
                });
    }

}
