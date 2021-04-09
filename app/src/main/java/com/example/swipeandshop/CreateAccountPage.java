package com.example.swipeandshop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountPage extends AppCompatActivity {

    EditText registerFullName, registerEmail, registerPhone, registerPassword;
    Button signUpButton;
    TextView gotoLogin;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_page);

        registerFullName = findViewById(R.id.editTextTextPersonName);
        registerEmail = findViewById(R.id.editTextTextEmailAddress2);
        registerPhone = findViewById(R.id.editTextPhone);
        registerPassword = findViewById(R.id.editTextTextPassword);
        signUpButton = findViewById(R.id.button);
        gotoLogin = findViewById(R.id.textView6);



        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // extract data

                String fullName = registerFullName.getText().toString();
                String email = registerEmail.getText().toString();
                String phone = registerPhone.getText().toString();
                String password = registerPassword.getText().toString();

                //validate data
                validate(fullName, email, phone, password);
            }
        });
    }

    protected void validate(String fullName, String email, String phone, String password) {
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
    }
}