package com.panagou.pms.android.project.pharmacyapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import java.util.Locale;

public class ProductDetailsActivity extends AppCompatActivity {

    private Product product;
    private int qty = 1;

    private TextView tvQty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        // Views
        TextView tvName  = findViewById(R.id.tvName);
        TextView tvPrice = findViewById(R.id.tvPrice);
        TextView tvDesc  = findViewById(R.id.tvDesc);
        tvQty            = findViewById(R.id.tvQty);
        ImageView iv     = findViewById(R.id.ivPhoto); // προαιρετικό
        Button btnMinus  = findViewById(R.id.btnMinus);
        Button btnPlus   = findViewById(R.id.btnPlus);
        Button btnAdd    = findViewById(R.id.btnAddToCart);

        // Πάρε δεδομένα από ProductsActivity
        String id    = getIntent().getStringExtra("id");
        String name  = getIntent().getStringExtra("name");
        double price = getIntent().getDoubleExtra("price", 0.0);
        String desc  = getIntent().getStringExtra("description");
        String img   = getIntent().getStringExtra("imageUrl");

        // Φτιάξε Product (με id!)
        product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        product.setDescription(desc);
        product.setImageUrl(img);

        // Γέμισε UI
        tvName.setText(name != null ? name : "-");
        tvPrice.setText(String.format(Locale.getDefault(), "€%.2f", price));
        tvDesc.setText(desc != null ? desc : "");
        tvQty.setText(String.valueOf(qty));

        // ΠΟΣΟΤΗΤΑ
        btnMinus.setOnClickListener(v -> {
            if (qty > 1) {
                qty--;
                tvQty.setText(String.valueOf(qty));
            }
        });

        btnPlus.setOnClickListener(v -> {
            qty++;
            tvQty.setText(String.valueOf(qty));
        });

        // ADD TO CART
        btnAdd.setOnClickListener(v -> {
            if (product.getId() == null || product.getId().isEmpty()) {
                Toast.makeText(this, "Missing product id", Toast.LENGTH_SHORT).show();
                return;
            }
            CartRepo.get().add(product, qty);
            int count = CartRepo.get().getItems().size();
            Log.d("CART", "After add: size=" + count + " (added " + qty + " of " + product.getName() + ")");
            Toast.makeText(this, "Added to cart (" + qty + ")", Toast.LENGTH_SHORT).show();
        });
    }
}
