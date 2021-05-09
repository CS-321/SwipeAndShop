package com.example.swipeandshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ChatPage extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder> myAdapter;
    FirebaseRecyclerAdapter<Chat, ChatViewHolder> chatAdapter;
    FirebaseRecyclerOptions<ChatMessage> options;
    FirebaseRecyclerOptions<Chat> chatOptions;
    ArrayAdapter<ChatMessage> itemsAdapter;
    ArrayList<ChatMessage> chatMessages;
    ChatMessageAdapter adapter;
    List<Chat> chats;
    RecyclerView productCardList;
    FirebaseUser user;
    RecyclerView chatCardList;
    DatabaseReference chatRef;
    DatabaseReference sellerChatRef;
    FirebaseDatabase database;
    Button closeChatBtn;
    Button sendMessageBtn;
    View chatView;
    Chat currentChat = null;
    ChatPage pg;

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
        pg = this;

        chatCardList = findViewById(R.id.chatsList); //holds all the users products
        database = FirebaseDatabase.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        chatRef = database.getReference().child("users").child(user.getUid()).child("chats");

        chatOptions =
                new FirebaseRecyclerOptions.Builder<Chat>()
                        .setQuery(chatRef,Chat.class).build();
        chats = new ArrayList<>();
        //load data.
        loadData();
        showData();
        setButtonListeners();
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

        chatAdapter = new FirebaseRecyclerAdapter<Chat, ChatViewHolder>(chatOptions) {

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_card,parent,false);
                return new ChatViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ChatViewHolder chatViewHolder, int i, @NonNull Chat chat) {
                chatViewHolder.name.setText(chat.getChatName());

                //Used to get which item is clicked on in the view.
                chatViewHolder.bindData(new ViewHolderCallback(){
                    @Override
                    public void itemWasClicked(int position){
                        // populate the text fields for creating/editing a product.
                        System.out.println(position + " items");
                        openChat(position);
                    }

                    @Override
                    public void itemToDelete(int position) {
                        // remove product from database and product list

                    }
                });
            }
        };

        //GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,1,GridLayoutManager.VERTICAL,false);
        chatCardList.setLayoutManager(gridLayoutManager);
        chatCardList.setAdapter(chatAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Start the adapter listener
        chatAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Stop the adapter listener
        chatAdapter.stopListening();
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
                    Chat tempChat = item_snapshot.getValue(Chat.class);
                    chats.add(tempChat);
                }


            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void setButtonListeners(){
        closeChatBtn = findViewById(R.id.closeChatBtn);
        sendMessageBtn = findViewById(R.id.sendMessageBtn);
        chatView = findViewById(R.id.chatCardView);
        chatView.setVisibility(View.GONE);
        closeChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatView.setVisibility(View.GONE);
                currentChat = null;
            }
        });

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText message = (EditText) findViewById(R.id.messageInputText);
                String msgText = message.getText().toString();
                System.out.println(msgText);
                sendMessage(msgText);
                message.setText("");
            }
        });
    }

    public void openChat(int position){
        currentChat = chats.get(position);
        ArrayList<ChatMessage> newChatMessages = new ArrayList<>();

        // Creating an ArrayList of values

        for(int i = 0; i < currentChat.messages.size(); i++){
            String check = "0" + Integer.toString(i+1);
            newChatMessages.add(currentChat.messages.get(check));
        }
        adapter = new ChatMessageAdapter(this, newChatMessages);
// Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.messageList);
        listView.setAdapter(adapter);
        chatView.setVisibility(View.VISIBLE);

        //load in information into adapter
        // This takes the loaded data from firebase and puts it into the adapter.

    }

    public void sendMessage(String message){
        if(currentChat != null && message.length() > 0){
            ChatMessage msg = new ChatMessage(message, user.getEmail());
            DatabaseReference newRef = chatRef.child(currentChat.getChatId()).child("messages");
            DatabaseReference otherRef = database.getReference().child("users").child(currentChat.getOtherPersonId()).child("chats").child(currentChat.getChatId2()).child("messages");
            String bob = "0"+ (currentChat.messages.size()+1);
            currentChat.messages.put(bob,msg);
            newRef.child(bob).setValue(msg);
            otherRef.child(bob).setValue(msg);
            ArrayList<ChatMessage> newChatMessages = new ArrayList<>();

            // Creating an ArrayList of values

            for(int i = 0; i < currentChat.messages.size(); i++){
                String check = "0" + Integer.toString(i+1);
                newChatMessages.add(currentChat.messages.get(check));
            }
            adapter = new ChatMessageAdapter(this, newChatMessages);
// Attach the adapter to a ListView
            ListView listView = (ListView) findViewById(R.id.messageList);
            listView.setAdapter(adapter);
            otherRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    HashMap<String, ChatMessage> testing = new HashMap<>();
                    if(currentChat != null){
                        System.out.println( "keyssss" + snapshot.getKey());
                        for(DataSnapshot item_snapshot:snapshot.getChildren()){
                            //Product tempProduct = item_snapshot.getValue(Chat.class);
                            // make sure user hasn't already swiped on this object.
                            ChatMessage cool  = item_snapshot.getValue(ChatMessage.class);
                            testing.put(item_snapshot.getKey(), cool);
                        }

                        for(int i = 0; i < testing.size(); i++){
                            String check = "0" + Integer.toString(i+1);
                            newChatMessages.add(testing.get(check));
                        }
                        adapter = new ChatMessageAdapter(adapter.getContext(), newChatMessages);
// Attach the adapter to a ListView
                        ListView listView = (ListView) findViewById(R.id.messageList);
                        listView.setAdapter(adapter);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

}