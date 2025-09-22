package com.panagou.pms.android.project.pharmacyapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

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
        ImageView iv     = findViewById(R.id.ivPhoto);
        ImageButton btnMinus  = findViewById(R.id.btnMinus);
        ImageButton btnPlus   = findViewById(R.id.btnPlus);
        Button btnAdd    = findViewById(R.id.btnAddToCart);

        // Extras από ProductsActivity
        String id    = getIntent().getStringExtra("id");
        String name  = getIntent().getStringExtra("name");
        double price = getIntent().getDoubleExtra("price", 0.0);
        String desc  = getIntent().getStringExtra("description");
        String img   = getIntent().getStringExtra("imageUrl"); // μπορεί να είναι URL ή όνομα drawable

        // Product
        product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        product.setDescription(desc);
        product.setImageUrl(img);

        // UI
        tvName.setText(name != null ? name : "-");
        tvPrice.setText(String.format(Locale.getDefault(), "€%.2f", price));
        tvDesc.setText(desc != null ? desc : "");
        tvQty.setText(String.valueOf(qty));

        // Φόρτωση εικόνας με Glide
        loadImageWithGlide(iv, img);


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

    private void loadImageWithGlide(ImageView iv, String img) {
        int placeholder = R.drawable.ic_launcher_background;

        if (img != null && (img.startsWith("http://") || img.startsWith("https://"))) {
            // Φόρτωση από URL
            Glide.with(this)
                    .load(img)
                    .placeholder(placeholder)
                    .error(placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(iv);
        } else if (img != null && !img.trim().isEmpty()) {

            int resId = getResources().getIdentifier(img.trim(), "drawable", getPackageName());
            if (resId != 0) {
                iv.setImageResource(resId);
            } else {

                iv.setImageResource(placeholder);
            }
        } else {

            iv.setImageResource(placeholder);
        }
    }
}
