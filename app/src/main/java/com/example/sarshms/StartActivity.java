package com.example.sarshms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sarshms.HospitalAdmin.LoginActivityHospital;
import com.example.sarshms.HospitalStaff.LoginActivityStaff;
import com.example.sarshms.User.LoginActivityUser;

public class StartActivity extends AppCompatActivity {

    private Button btnLoginUser, btnLoginStaff , btnLoginHospital;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_layout);

        // Initialize buttons
        btnLoginUser = findViewById(R.id.btnLoginUser);
        btnLoginStaff = findViewById(R.id.btnLoginStaff);
        btnLoginHospital=findViewById(R.id.btnLoginHospital);

        // Navigate to User Login
        btnLoginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(StartActivity.this, LoginActivityUser.class);
                startActivity(userIntent);
            }
        });

        // Navigate to Staff Login
        btnLoginStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent staffIntent = new Intent(StartActivity.this, LoginActivityStaff.class);
                startActivity(staffIntent);
            }
        });


        btnLoginHospital.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, LoginActivityHospital.class);
            startActivity(intent);
        });

    }
}

