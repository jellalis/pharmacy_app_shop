package com.panagou.pms.android.project.pharmacyapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProductDetailsActivity extends AppCompatActivity {

    private Product current;
    private int qty = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        // Πάρε τα extras από το intent
        String id    = getIntent().getStringExtra("id");
        String name  = getIntent().getStringExtra("name");
        double price = getIntent().getDoubleExtra("price", 0.0);
        String desc  = getIntent().getStringExtra("description");
        String image = getIntent().getStringExtra("imageUrl");

        current = new Product();
        current.setId(id);
        current.setName(name);
        current.setPrice(price);
        current.setDescription(desc);
        current.setImageUrl(image);

        TextView tvName  = findViewById(R.id.tvName);
        TextView tvPrice = findViewById(R.id.tvPrice);
        TextView tvDesc  = findViewById(R.id.tvDesc);
        TextView tvQty   = findViewById(R.id.tvQty);
        ImageView ivImg  = findViewById(R.id.ivImage); // προαιρετικά, αν θες εικόνα αργότερα

        tvName.setText(name != null ? name : "-");
        tvPrice.setText(String.format("€%.2f", price));
        tvDesc.setText(desc != null ? desc : "-");
        tvQty.setText(String.valueOf(qty));

        Button btnMinus = findViewById(R.id.btnMinus);
        Button btnPlus  = findViewById(R.id.btnPlus);
        Button btnAdd   = findViewById(R.id.btnAddToCart);

        btnMinus.setOnClickListener(v -> {
            qty = Math.max(1, qty - 1);
            tvQty.setText(String.valueOf(qty));
        });

        btnPlus.setOnClickListener(v -> {
            qty += 1;
            tvQty.setText(String.valueOf(qty));
        });

        btnAdd.setOnClickListener(v -> {
            CartRepo.get().add(current, qty);
            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
            // προαιρετικά: finish();
        });
    }
}
