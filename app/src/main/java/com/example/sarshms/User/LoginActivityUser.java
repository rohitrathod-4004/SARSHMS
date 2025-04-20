package com.example.sarshms.User;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sarshms.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivityUser extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewSignup;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        // ✅ Initialize Firebase before using it
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewSignup = findViewById(R.id.textViewSignup);

        buttonLogin.setOnClickListener(v -> loginUser());
        textViewSignup.setOnClickListener(v ->
                startActivity(new Intent(LoginActivityUser.this, SignupActivityUser.class))
        );
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Email and Password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String userEmail = user.getEmail();

                    // 🔥 Now fetch the username from Firestore
                    FirebaseFirestore.getInstance()
                            .collection("Users")
                            .whereEqualTo("email", userEmail)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    String username = queryDocumentSnapshots.getDocuments()
                                            .get(0)
                                            .getString("username");

                                    if (username != null) {
                                        // ✅ Save username in SharedPreferences
                                        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putString("username", username);
                                        editor.apply();

                                        Toast.makeText(LoginActivityUser.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivityUser.this, MainActivityUser.class));
                                        finish();
                                    } else {
                                        Toast.makeText(this, "Username not found for this user", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(this, "User record not found", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error fetching username", Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Toast.makeText(LoginActivityUser.this, "Login Failed. Check your credentials.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
