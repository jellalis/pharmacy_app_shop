package com.panagou.pms.android.project.pharmacyapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "REGISTER"; // φίλτρο στο Logcat

    private EditText etFirstName, etLastName, etEmail, etPhone, etPassword;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.register);

        View root = findViewById(R.id.main);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                return insets;
            });
        }

        etFirstName = findViewById(R.id.editTextText);              //  name
        etLastName  = findViewById(R.id.editTextText2);             // surname
        etEmail     = findViewById(R.id.editTextTextEmailAddress2); // email
        etPhone     = findViewById(R.id.editTextPhone);             // phone
        etPassword  = findViewById(R.id.editTextTextPassword2);     // password

        auth = FirebaseAuth.getInstance();
        db   = FirebaseFirestore.getInstance();

        android.util.Log.d(TAG, "RegisterActivity created. Firebase ready");
    }


    public void signup(View view) {
        String first = text(etFirstName);
        String last  = text(etLastName);
        String email = text(etEmail);
        String phone = text(etPhone);
        String pass  = text(etPassword);

        android.util.Log.d(TAG, "signup clicked. email=" + email);

        if (email.isEmpty()) { Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show(); return; }
        if (pass.length() < 6) { Toast.makeText(this, "Password ≥ 6 chars", Toast.LENGTH_SHORT).show(); return; }

        view.setEnabled(false);

        auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(task -> {
                    String uid = (task.getUser() != null) ? task.getUser().getUid() : null;
                    android.util.Log.d(TAG, "Auth createUser SUCCESS. uid=" + uid);

                    String fullName = (first + " " + last).trim();
                    User newUser = new User(fullName, email, phone, "customer");

                    db.collection("users").document(uid)
                            .set(newUser)
                            .addOnSuccessListener(v -> {
                                view.setEnabled(true);
                                android.util.Log.d(TAG, "Firestore write SUCCESS for uid=" + uid);
                                Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show();
                                finish(); // επιστροφή στο Login
                            })
                            .addOnFailureListener(e -> {
                                view.setEnabled(true);
                                android.util.Log.e(TAG, "Firestore write FAILED", e);
                                Toast.makeText(this, "Save profile failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    view.setEnabled(true);
                    android.util.Log.e(TAG, "Auth createUser FAILED", e);
                    Toast.makeText(this, "Signup failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private String text(EditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}
