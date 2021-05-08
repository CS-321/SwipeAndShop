package com.example.swipeandshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SettingsPage extends AppCompatActivity {

    public void logOut(View view) {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("General Settings");
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            try {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("users/" + uid + "/" + key);
                switch (key) {
                    case "BlockUsers":
                    case "EnableNotifications":
                        myRef.setValue(sharedPreferences.getBoolean(key, false));
                        break;
                    default:
                        myRef.setValue(sharedPreferences.getString(key, "null"));
                }
                myRef.setValue(sharedPreferences.getString(key, "null"));
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        private String name, email, uid;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                name = user.getDisplayName();
                email = user.getEmail();
                boolean emailVerified = user.isEmailVerified();
                uid = user.getUid();
            } else
                return;

            PreferenceManager preferenceManager = getPreferenceManager();
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            DatabaseReference mDatabase;
            mDatabase = FirebaseDatabase.getInstance().getReference();

            EditTextPreference textPreference = (EditTextPreference) preferenceManager.findPreference("name");
            Task<DataSnapshot> t = mDatabase.child("users").child(uid).child("name").get();
            EditTextPreference finalTextPreference = textPreference;
            t.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        finalTextPreference.setText(String.valueOf(task.getResult().getValue()));
                    }
                }
            });

            textPreference = (EditTextPreference) preferenceManager.findPreference("UserAddress");
            t = mDatabase.child("users").child(uid).child("UserAddress").get();
            EditTextPreference finalTextPreference1 = textPreference;
            t.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        finalTextPreference1.setText(String.valueOf(task.getResult().getValue()));
                    }
                }
            });

            SwitchPreferenceCompat sw = (SwitchPreferenceCompat) preferenceManager.findPreference("BlockUsers");
            t = mDatabase.child("users").child(uid).child("BlockUsers").get();
            SwitchPreferenceCompat finalsw = sw;
            t.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        finalsw.setChecked(String.valueOf(task.getResult().getValue()) == "true" ? true : false);
                    }
                }
            });

            sw = (SwitchPreferenceCompat) preferenceManager.findPreference("EnableNotifications");
            t = mDatabase.child("users").child(uid).child("EnableNotifications").get();
            SwitchPreferenceCompat finalsw2 = sw;
            t.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        finalsw2.setChecked(String.valueOf(task.getResult().getValue()) == "true" ? true : false);
                    }
                }
            });

        }
    }
}