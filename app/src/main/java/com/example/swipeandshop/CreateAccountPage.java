package com.example.swipeandshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// import com.google.firebase.auth.

public class CreateAccountPage extends AppCompatActivity {

    EditText registerFullName, registerEmail, registerPhone, registerPassword, registerVerifyPassword;
    Button signUpButton;
    TextView gotoLogin;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_page);

        registerFullName = findViewById(R.id.fullName);
        registerEmail = findViewById(R.id.editTextTextEmailAddress2);
        registerPhone = findViewById(R.id.editTextPhone);
        registerPassword = findViewById(R.id.editTextTextPassword);
        registerVerifyPassword = findViewById(R.id.verifyPassword);
        signUpButton = findViewById(R.id.button);
        gotoLogin = findViewById(R.id.textView6);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();



        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // extract data

                String fullName = registerFullName.getText().toString();
                String email = registerEmail.getText().toString();
                String phone = registerPhone.getText().toString();
                String password = registerPassword.getText().toString();
                String verifyPassword = registerVerifyPassword.getText().toString();

                //validate data

                if(fullName.isEmpty()) {
                    registerFullName.setError("Full Name is required");
                    return;
                }
                if(email.isEmpty()) {
                    registerEmail.setError("Email is required");
                    return;
                }
                if(phone.isEmpty()) {
                    registerPhone.setError("Phone number is required");
                    return;
                }
                if(password.isEmpty()) {
                    registerPassword.setError("Password is required");
                    return;
                }

                if(password.length()<6) {
                    registerPassword.setError("Password needs to be at least 6 characters long");
                    return;
                }

                if(!password.equals(verifyPassword)) {
                    registerVerifyPassword.setError("Passwords don't match");
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        user = firebaseAuth.getCurrentUser();
                        myRef.child("users").child(user.getUid()).child("name").setValue(fullName);
                        myRef.child("users").child(user.getUid()).child("phone number").setValue(phone);
                        startActivity(new Intent(getApplicationContext(), ProductPage.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        registerEmail.setError("Email already in use");
                    }
                });


            }
        });

        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginPage.class));
            }
        });
    }
}