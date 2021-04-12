package com.example.swipeandshop;

import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsPage extends AppCompatActivity {

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
    //Add Custom user preference DataStore in Firebase Realtime DB.
    //I  can save user preferences and we can see it in realtime database
    // It is supposed to be used by Android preference manager to overwrite
    // where settings are stored instead of default location.
    public static class DataStore extends PreferenceDataStore {

        // Save user preference in Firebase DB.
        @Override
        public void putString(String key, @Nullable String value) {
            try {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(key);
                myRef.setValue(value);

            } catch (Exception e){
                System.out.println(e);
            }
        }
        final String[] s = new String[1];

        // Load user preference from DB to Android.
        //I am running into database firebase problem.
        // A problem with Firebase reads is it's asynchronous. I can't read user setting from DB
        // and wait for response. A response might come back any time.
        // We need to read values from DB once and synchronously.
        @Override
        @Nullable
        public String getString(String key, @Nullable String defValue) {
            DatabaseReference mDatabase;
            mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child(key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

                // This method executes asynchronously. I can't read DB with this predictably.
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                    else {
                        s[0] = String.valueOf(task.getResult().getValue());
                        System.out.println("READ BACK " + s[0]); // this prints fine but some time in the future
                    }
                }
            });
            // Even this sleep doesn't wait for onComplete.
            try {
                Thread.sleep(3000);
            }
            catch (Exception e){}
            System.out.println("READ BACK 2 " + s[0]); // this print null because it's always executed before onComplete
            return s[0];
        }
    }


    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            // Create preference manager based on Firebase backed datastore.
            PreferenceManager preferenceManager = getPreferenceManager();
            DataStore dataStore = new DataStore();
            preferenceManager.setPreferenceDataStore(dataStore);

        }
    }
}