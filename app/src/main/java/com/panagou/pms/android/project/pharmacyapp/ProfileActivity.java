package com.panagou.pms.android.project.pharmacyapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_page);

        TextView title = findViewById(R.id.tvPageTitle);
        title.setText("Profile Page");

        BottomNavigationView bottom = findViewById(R.id.bottom_nav);
        bottom.setSelectedItemId(R.id.nav_profile);

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
                startActivity(new Intent(this, OrdersActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                return true; // ήδη εδώ
            }
            return false;
        });
    }
}
