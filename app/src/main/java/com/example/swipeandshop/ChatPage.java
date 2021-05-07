package com.example.swipeandshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatPage extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder> myAdapter;
    FirebaseRecyclerOptions<ChatMessage> options;
    List<ChatMessage> chatMessages;
    List<Chat> chats;
    DatabaseReference chatRef;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);
        mFirebaseAuth = FirebaseAuth.getInstance();
        if (mFirebaseAuth.getCurrentUser() == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, LoginPage.class));
            finish();
            return;
        }
        database = FirebaseDatabase.getInstance();
        chatRef = database.getReference().child("chats");
        /*options =
                new FirebaseRecyclerOptions.Builder<ChatMessage>()
                        .setQuery(chatRef,ChatMessage.class).build();
        showData();*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void openSettings(View view) {
        Intent intent = new Intent(ChatPage.this, SettingsPage.class);
        startActivity(intent);
    }

    public void openFeed(View view) {
        Intent intent = new Intent(ChatPage.this, FeedPage.class);
        startActivity(intent);
    }

    public void openUpload(View view) {
        Intent intent = new Intent(ChatPage.this, ProductPage.class);
        startActivity(intent);
    }

    public void showData() {
        // This takes the loaded data from firebase and puts it into the adapter.
        myAdapter = new FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder>(options) {
            @NonNull
            @Override
            public ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message,parent,false);
                return new ChatMessageViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ChatMessageViewHolder chatMessageViewHolder, int i, @NonNull ChatMessage chatMessage) {
                //put all the data in the view.
            }
        };
    }

    /**Retrieves data from firebase when page is first loaded.*/
    public void loadData(){
        // this will load data from firebase and put it into the products list.
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item_snapshot:snapshot.getChildren()){
                    //Product tempProduct = item_snapshot.getValue(Chat.class);
                    // make sure user hasn't already swiped on this object.
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}