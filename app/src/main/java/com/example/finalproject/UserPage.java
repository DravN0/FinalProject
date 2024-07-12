package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class UserPage extends AppCompatActivity {

    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.userpage);
        init();
    }

    private void init(){
        btnLogout = findViewById(R.id.btnLogout);
        setOnClickListener();  // Ensure to call setOnClickListener() here
    }

    private void setOnClickListener() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateToast("Logout");
                startActivity(new Intent(UserPage.this, MainActivity.class));
                finish();  // Optional: close the current activity to prevent going back to it
            }
        });
    }

    private void CreateToast(String message) {
        Toast.makeText(UserPage.this, message, Toast.LENGTH_SHORT).show();
    }
}
