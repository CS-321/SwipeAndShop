package com.example.swipeandshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import static android.os.SystemClock.sleep;

public class MainActivity extends AppCompatActivity {

    Button gotoRegister, gotoLogin, goToChat, goToFeed, goToProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login_page);
        startActivity(new Intent(getApplicationContext(), LoginPage.class));
    }
}