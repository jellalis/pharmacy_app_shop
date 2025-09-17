package com.panagou.pms.android.project.pharmacyapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class OrdersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_page);

        // 1) Βάζουμε τίτλο
        TextView title = findViewById(R.id.tvPageTitle);
        title.setText("Orders Page");

        // 2) Bottom navigation
        BottomNavigationView bottom = findViewById(R.id.bottom_nav);
        bottom.setSelectedItemId(R.id.nav_orders);

        bottom.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_products) {
                startActivity(new Intent(this, ProductsActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_cart) {
                startActivity(new Intent(this, CartActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_orders) {
                return true; // ήδη εδώ
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }
}
