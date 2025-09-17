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
}
