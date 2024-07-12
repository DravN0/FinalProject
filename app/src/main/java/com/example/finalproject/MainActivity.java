package com.example.finalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import com.example.finalproject.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin;
    private TextInputEditText txtUsername;
    private TextInputEditText txtPassword;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setClickListener();
    }

    private void init(){
        btnLogin = findViewById(R.id.btnlogin);
        txtUsername = findViewById(R.id.loginUsername);
        txtPassword = findViewById(R.id.loginPassword);
        db = FirebaseFirestore.getInstance(); // Initialize FirebaseFirestore
    }

    private void determineUserType(String username, String password) {
        db.collection("Users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean userFound = false;
                    User existingUser = null;

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        User user = doc.toObject(User.class);

                        if (user.getName().equals(username) && user.getPassword().equals(password)) {
                            existingUser = user;
                            userFound = true;
                            break;
                        }
                    }

                    if (!userFound) {
                        Toast.makeText(MainActivity.this, "User doesn't exist", Toast.LENGTH_SHORT).show();
                    } else if (existingUser.getUserType().equals("User")) {
                        Intent intent = new Intent(MainActivity.this, UserPage.class);
                        intent.putExtra("USERNAME", username);
                        startActivity(new Intent(getBaseContext(), UserPage.class));
                    } else if (existingUser.getUserType().equals("Admin")) {
                        startActivity(new Intent(getBaseContext(), AdminPage.class));
                    } else if (existingUser.getUserType().equals("SuperAdmin")) {
                        startActivity(new Intent(getBaseContext(), SuperAdmin.class));
                    } else {
                        Toast.makeText(MainActivity.this, "Unknown user type", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setClickListener(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();

                if(username.isEmpty()){
                    txtUsername.setError("No account found");
                    Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.isEmpty()){
                    txtPassword.setError("Incorrect password");
                    Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    return;
                }

                determineUserType(username, password);
            }
        });
    }
}
