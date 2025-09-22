package com.panagou.pms.android.project.pharmacyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "AUTH"; // φίλτρο στο Logcat

    private EditText etEmail, etPassword;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);


        View root = findViewById(R.id.main);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                return insets;
            });
        }

        etEmail    = findViewById(R.id.editTextTextEmailAddress);
        etPassword = findViewById(R.id.editTextTextPassword);
        auth       = FirebaseAuth.getInstance();


        Button btnLogin = findViewById(R.id.button3);
        if (btnLogin != null) btnLogin.setOnClickListener(this::doLogin);

        android.util.Log.d(TAG, "LoginActivity created. FirebaseAuth inited");
    }


    public void doLogin(View v) {
        String email = text(etEmail);
        String pass  = text(etPassword);

        android.util.Log.d(TAG, "doLogin clicked. email=" + email);

        if (email.isEmpty()) { Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show(); return; }
        if (pass.length() < 6) { Toast.makeText(this, "Password ≥ 6 chars", Toast.LENGTH_SHORT).show(); return; }

        v.setEnabled(false);

        auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(r -> {
                    Intent i = new Intent(this, ProductsActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });

    }

    private String text(EditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}
