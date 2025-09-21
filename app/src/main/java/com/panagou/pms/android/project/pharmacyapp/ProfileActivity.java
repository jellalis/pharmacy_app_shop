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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvPhone, tvRole, tvCreated, tvUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        View root = findViewById(R.id.rootProfile);
        BottomNavigationView bottom = findViewById(R.id.bottom_nav);

        // Edge-to-edge insets
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

        // Bottom nav
        if (bottom != null) {
            bottom.setSelectedItemId(R.id.nav_profile);
            bottom.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_products) {
                    startActivity(new Intent(this, ProductsActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                    return true;
                }
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
                if (id == R.id.nav_profile) return true;
                return false;
            });
        }

        // Views
        tvName    = findViewById(R.id.tvName);
        tvEmail   = findViewById(R.id.tvEmail);
        tvPhone   = findViewById(R.id.tvPhone);
        tvRole    = findViewById(R.id.tvRole);
        tvCreated = findViewById(R.id.tvCreated);
        tvUpdated = findViewById(R.id.tvUpdated);

        // Logout
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        loadUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottom = findViewById(R.id.bottom_nav);
        if (bottom != null) bottom.setSelectedItemId(R.id.nav_profile);
    }

    private void loadUser() {
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        if (u == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        String uid = u.getUid();

        // Δείξε email άμεσα (ακόμα κι αν δεν υπάρχει user doc)
        tvEmail.setText("Email: " + (u.getEmail() == null ? "-" : u.getEmail()));

        // Αν κρατάς προφίλ στο Firestore: collection "users" -> doc(uid)
        FirebaseFirestore.getInstance()
                .collection("users").document(uid)
                .get()
                .addOnSuccessListener(this::bindUser)
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Profile load failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void bindUser(DocumentSnapshot d) {
        if (d == null || !d.exists()) {
            // Δεν υπάρχει user doc – κρατάμε μόνο το email που βάλαμε ήδη
            return;
        }
        tvName.setText("Name: "   + nn(d.getString("name")));
        tvPhone.setText("Phone: " + nn(d.getString("phone")));
        tvRole.setText("Role: "   + nn(d.getString("role")));

        // 🔁 χρησιμοποίησε τα σωστά ονόματα πεδίων
        tvCreated.setText("Created: " + fmt(d.getTimestamp("createdTime")));
        tvUpdated.setText("Updated: " + fmt(d.getTimestamp("updatedTime")));
    }

    private String nn(String s) {
        return (s == null || s.isEmpty()) ? "-" : s;
    }

    private String fmt(Timestamp ts) {
        if (ts == null) return "-";
        return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .format(ts.toDate());
    }
}
