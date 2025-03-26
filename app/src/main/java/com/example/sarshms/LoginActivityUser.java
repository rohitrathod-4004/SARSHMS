package com.example.sarshms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sarshms.User.SignupActivityUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class LoginActivityUser extends AppCompatActivity {

    private EditText etName, etUsername, etMobile, etBirthdate, etBloodGroup, etPassword;
    private Button btnSignup;
    private FirebaseFirestore db;
    private CollectionReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_user);

        etName = findViewById(R.id.et_name);
        etUsername = findViewById(R.id.et_username);
        etMobile = findViewById(R.id.et_mobile);
        etBirthdate = findViewById(R.id.et_birthdate);
        etBloodGroup = findViewById(R.id.sp_bloodgroup);
        etPassword = findViewById(R.id.et_password);
        btnSignup = findViewById(R.id.btn_signup);
        TextView tvLogin = findViewById(R.id.tv_login);

        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("Users");

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivityUser.this, SignupActivityUser.class));

            }
        });
    }

    private void registerUser() {
        final String name = etName.getText().toString().trim();
        final String username = etUsername.getText().toString().trim();
        final String mobile = etMobile.getText().toString().trim();
        final String birthdate = etBirthdate.getText().toString().trim();
        final String bloodGroup = etBloodGroup.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || username.isEmpty() || mobile.isEmpty() || birthdate.isEmpty() || bloodGroup.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        usersRef.document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && !task.getResult().exists()) {
                    Map<String, Object> user = new HashMap<>();
                    user.put("name", name);
                    user.put("username", username);
                    user.put("mobile", mobile);
                    user.put("birthdate", birthdate);
                    user.put("bloodGroup", bloodGroup);
                    user.put("password", password);

                    usersRef.document(username).set(user).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(LoginActivityUser.this, "Signup Successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivityUser.this, LoginActivityUser.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivityUser.this, "Error! Try Again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(LoginActivityUser.this, "Username already exists!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
