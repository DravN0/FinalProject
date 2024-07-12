package com.example.finalproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.adapters.UserAdapter;
import com.example.finalproject.models.User;
import com.example.finalproject.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SuperAdmin extends AppCompatActivity {

    private String TAG = "SuperAdmin";
    private FloatingActionButton btnAdd;
    private FirebaseFirestore db;
    private String userType;
    private boolean hasError = false;
    private ListView listUsers;
    private UserAdapter userAdapter;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.superadmin);
        init();
        setOnClickListener();
        loadUsers();
    }

    private void init(){
        //Firebase
        db = FirebaseFirestore.getInstance();
        btnAdd = findViewById(R.id.btnFloatingAdd);
        listUsers = findViewById(R.id.listUsers);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setOnClickListener() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog();
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateToast("Logout");
                startActivity(new Intent(SuperAdmin.this, MainActivity.class));
            }
        });
    }

    private void loadUsers(){
        // Assuming 'db' is your FirebaseFirestore instance
        db.collection("Users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Users> users = new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getId();
                        User user = document.toObject(User.class);

                        Users userData = new Users(id, user);
                        users.add(userData);
                    }

                    // Create the adapter and set it to the ListView
                    UserAdapter adapter = new UserAdapter(this, users);
                    ListView listUsers = findViewById(R.id.listUsers);
                    listUsers.setAdapter(adapter);

                    // Handle item clicks
                    listUsers.setOnItemClickListener((parent, view, position, id) -> {
                        // Handle click on item
                        Users selectedItem = users.get(position);
                        updateDialog(selectedItem);
                    });

                    //Handle item long click
                    listUsers.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Users selectedUser = users.get(i);
                            deleteDialog(selectedUser);
                            return true;
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Log.e("TAG", "Error fetching items", e);
                });
    }

    private void createDialog(){
        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add user");

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.user_layout, null);
        builder.setView(customLayout);

        // send data from the AlertDialog to the Activity
        TextInputLayout txtUsername = customLayout.findViewById(R.id.txtUsername);
        TextInputLayout txtPassword = customLayout.findViewById(R.id.txtPassword);
        RadioButton radioAdmin = customLayout.findViewById(R.id.radioAdmin);
        RadioButton radioUser = customLayout.findViewById(R.id.radioUser);

        //Radio Button
        radioAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userType = "Admin";
            }
        });
        radioUser.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                userType = "User";
            }
        });

        // add a button
        builder.setPositiveButton("Add", null);
        builder.setNegativeButton("Cancel", null);
        builder.create();

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String name = txtUsername.getEditText().getText().toString();
                        String password = txtPassword.getEditText().getText().toString();

                        if(TextUtils.isEmpty(name)){
                            txtUsername.setError("* Required!");
                            return;
                        }
                        if(TextUtils.isEmpty(password)){
                            txtPassword.setError("* Required!");
                            return;
                        }
                        if(userType.isEmpty()){
                            CreateToast("User type needed.");
                            return;
                        }

                        if(!addDataToFirestore(name, password, userType)){
                            loadUsers();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        dialog.show();
    }

    private void updateDialog(Users user) {
        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(user.getUser().getName());

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.user_update_layout, null);
        builder.setView(customLayout);

        // send data from the AlertDialog to the Activity
        EditText txtUsername = customLayout.findViewById(R.id.txtUsername);
        EditText txtPassword = customLayout.findViewById(R.id.txtPassword);

        // add a button
        builder.setPositiveButton("Update", null);
        builder.setNegativeButton("Cancel", null);
        builder.create();

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String name = txtUsername.getText().toString();
                        String password = txtPassword.getText().toString();

                        if(TextUtils.isEmpty(name)){
                            txtUsername.setError("* Required!");
                            return;
                        }

                        if(TextUtils.isEmpty(password)){
                            txtPassword.setError("* Required!");
                            return;
                        }

                        User updateUser = new User(name, password, user.getUser().getUserType());
                        Users toUpdateUser = new Users(user.getId(), updateUser);

                        if(!updateData(toUpdateUser)){
                            loadUsers();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        dialog.show();
    }

    private void deleteDialog(Users user){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete?");
        builder.setMessage("Are you sure you want to delete this user?\n\n" + user.getUser().getName());
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", null);
        builder.setNegativeButton("No", null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button btnYes = (dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!deleteData(user.getId())){
                            loadUsers();
                            dialog.dismiss();
                        }
                    }
                });

                Button btnNo = (dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    private boolean addDataToFirestore(String name, String password, String userType){
        CollectionReference dbUsers = db.collection("Users");

        //Add to object
        User user = new User(name, password, userType);

        //Saving to firestore
        dbUsers.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                CreateToast("User has been added!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CreateToast("Fail to add user \n" + e);
                hasError = true;
            }
        });

        return hasError;
    }

    private boolean updateData(Users user){
        db.collection("Users")
                .document(user.getId())
                .set(user.getUser())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        CreateToast("User has been updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        CreateToast("Failed to update user\n" + e);
                        hasError = true;
                    }
                });

        return hasError;
    }

    private boolean deleteData(String id) {
        db.collection("Users")
                .document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        CreateToast("User has been deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        CreateToast("Failed to delete user\n" + e);
                        hasError = true;
                    }
                });

        return hasError;
    }

    private void CreateToast(String message){
        Toast.makeText(SuperAdmin.this, message, Toast.LENGTH_SHORT).show();
    }
}