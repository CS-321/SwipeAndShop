package com.example.swipeandshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;

import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FeedPage extends AppCompatActivity {
    private static final String TAG = "FeedPage";
    private static DecimalFormat df = new DecimalFormat("0.00");
    private CardStackLayoutManager manager;
    private ProductCardStackAdapter adapter;


    // Firebase references.
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference allProductRef;
    DatabaseReference userRef;
    DatabaseReference chatRef;
    FirebaseUser user;
    List<Product> products;
    HashMap<String, Product> likedProducts;
    HashMap<String, Product> dislikedProducts;
    HashMap<String, Chat> chats;
    Product currentProduct;

    // For more info.
    ConstraintLayout constraintLayout;
    ConstraintSet constraintSetOld = new ConstraintSet();
    ConstraintSet constraintSetNew = new ConstraintSet();
    boolean isClicked = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_page);
        //Database
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = firebaseAuth.getCurrentUser();
        allProductRef = database.getReference().child("products");
        userRef = database.getReference().child("users").child(user.getUid());
        chatRef = database.getReference().child("users").child(user.getUid()).child("chats");

        currentProduct = null;
        products = new ArrayList<>();
        likedProducts = new HashMap<>();
        dislikedProducts = new HashMap<>();
        chats = new HashMap<>();

        //Loads in data to adapter from firebase.
        constraintLayout = findViewById(R.id.myLayout);
        constraintSetOld.clone(constraintLayout);
        constraintSetNew.clone(this, R.layout.activity_feed_page_alt);

        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void paginate() {
        List<Product> old = adapter.getItems();
        List<Product> newer = new ArrayList<>(addList());
        ProductCardStackCallback callback = new ProductCardStackCallback(old, newer);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        adapter.setItems(newer);
        result.dispatchUpdatesTo(adapter);
    }

    private List<Product> addList() {
        return products;
    }

    /**Retrieves data from firebase when page is first loaded
     * and sets listener for when data is chhanged.*/
    public void loadData(){
        //Get viewed products by a person so they don't view them again.
        //Called everytime a product is added or removed from the db.
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get all liked products from the database.
                for(DataSnapshot item_snapshot:snapshot.child("likedProducts").getChildren()){
                    Product tempProduct = item_snapshot.getValue(Product.class);
                    likedProducts.put(tempProduct.productId, tempProduct);
                }

                for(DataSnapshot item_snapshot:snapshot.child("dislikedProducts").getChildren()){
                    Product tempProduct = item_snapshot.getValue(Product.class);
                    dislikedProducts.put(tempProduct.productId, tempProduct);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // this will load data from firebase and put it into the products list.
        //Called everytime a product is added or removed from the db.
        allProductRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                products.clear();
                for(DataSnapshot item_snapshot:snapshot.getChildren()){
                    Product tempProduct = item_snapshot.getValue(Product.class);
                    products.add(tempProduct);
                }
                setAdapterInfo();
            }




            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chats.clear();
                for(DataSnapshot item_snapshot:snapshot.getChildren()){
                    System.out.println(snapshot.getValue().toString());
                    Chat tempChat = item_snapshot.getValue(Chat.class);
                    chats.put(tempChat.getChatId() ,tempChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**Sets adapter info for the swiping cards.*/
    private void setAdapterInfo(){
        CardStackView cardStackView = findViewById(R.id.card_stacked_view);

        manager = new CardStackLayoutManager(FeedPage.this, new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                //Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + " d=" + direction);
                if (direction == Direction.Right){
                    Toast.makeText(FeedPage.this, "Direction Right", Toast.LENGTH_SHORT).show();
                    cardSwipedRight(); //do stuff for when product is swiped right
                }
                if (direction == Direction.Top){
                    Toast.makeText(FeedPage.this, "Direction Top", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Left){
                    Toast.makeText(FeedPage.this, "Direction Left", Toast.LENGTH_SHORT).show();
                    cardSwipedLeft(); //do stuff for when product is swiped left
                }
                if (direction == Direction.Bottom){
                    Toast.makeText(FeedPage.this, "Direction Bottom", Toast.LENGTH_SHORT).show();
                }

                // Paginating
                if (manager.getTopPosition() == adapter.getItemCount() - 5){
                    paginate();
                }

            }

            @Override
            public void onCardRewound() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardCanceled() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardAppeared(View view, int position) {
                //set product when it first loads up
                TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", name: " + tv.getText());
                currentProduct = products.get(position);
                CardView cardView = findViewById(R.id.cardid);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //open up new set up.
                        swapView(v);
                    }
                });
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", name: " + tv.getText());
            }
        });
        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.HORIZONTAL); //Change to HORIZONTAL if only swiping left/right
        manager.setCanScrollHorizontal(true);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        adapter = new ProductCardStackAdapter(addList());
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());

    }

    private void cardSwipedRight(){
        if(currentProduct != null){
            likedProducts.put(currentProduct.productId,currentProduct);
            String chatId = currentProduct.getSellerId() + user.getUid().toString();
            String chatId2 = user.getUid().toString() + currentProduct.getSellerId();
            userRef.child("likedProducts").child(currentProduct.productId).setValue(currentProduct);
            if(!chats.containsKey(chatId)){
                DatabaseReference sellerRef = database.getReference().child("users").child(currentProduct.getSellerId());
                System.out.println("This chat does not exist.");
                Chat newChat = new Chat(chatId, chatId2, currentProduct.getSellerId(),currentProduct.getSeller() + " Chat",new HashMap<>());
                Chat newChatSeller = new Chat(chatId2,chatId, user.getUid(),user.getEmail() + " Chat",new HashMap<>());
                chatRef.child(chatId).setValue(newChat);
                //userRef.child("chats").child(chatId).setValue(newChat);
                sellerRef.child("chats").child(chatId2).setValue(newChatSeller);
            }
        }
    }

    private void cardSwipedLeft(){
        if(currentProduct != null){
            dislikedProducts.put(currentProduct.productId,currentProduct);
            userRef.child("dislikedProducts").child(currentProduct.productId).setValue(currentProduct);
        }
    }

    public void swapView(View v){
        TransitionManager.beginDelayedTransition(constraintLayout);
        TextView card_price = v.findViewById(R.id.item_price);
        TextView card_desc = v.findViewById(R.id.item_desc);
        TextView card_name = v.findViewById(R.id.item_name);

        if(currentProduct != null){
            if(!isClicked){
                constraintSetNew.applyTo(constraintLayout);
                TextView seller_text = findViewById(R.id.seller_text);
                TextView name_text = findViewById(R.id.name_text);
                TextView price_text = findViewById(R.id.price_text);
                TextView description_text = findViewById(R.id.description_text);

                card_price.setVisibility(View.INVISIBLE);
                card_desc.setVisibility(View.INVISIBLE);
                card_name.setVisibility(View.INVISIBLE);

                seller_text.setText(currentProduct.getSeller());
                name_text.setText(currentProduct.getName());
                price_text.setText("$" + df.format(currentProduct.getPrice()));
                description_text.setText(currentProduct.getLongDescription());

                manager.setCanScrollHorizontal(false);
                manager.setCanScrollVertical(false);
                isClicked = true;
            }else{
                card_price.setVisibility(View.VISIBLE);
                card_desc.setVisibility(View.VISIBLE);
                card_name.setVisibility(View.VISIBLE);
                constraintSetOld.applyTo(constraintLayout);
                manager.setCanScrollHorizontal(true);
                manager.setCanScrollVertical(true);
                isClicked = false;
            }
        }

    }

    public void openChat(View view) {
        Intent intent = new Intent(FeedPage.this, ChatPage.class);
        startActivity(intent);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(FeedPage.this, SettingsPage.class);
        startActivity(intent);
    }

    public void openUpload(View view) {
        Intent intent = new Intent(FeedPage.this, ProductPage.class);
        startActivity(intent);
    }


}