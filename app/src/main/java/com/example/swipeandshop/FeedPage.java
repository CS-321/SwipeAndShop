package com.example.swipeandshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;

import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;


public class FeedPage extends AppCompatActivity {
    private static final String TAG = "FeedPage";
    private CardStackLayoutManager manager;
    private ProductCardStackAdapter adapter;

    // Firebase references.
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference allProductRef;
    FirebaseUser user;
    List<Product> products;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_page);
        //Database
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = firebaseAuth.getCurrentUser();
        allProductRef = database.getReference().child("products");
        products = new ArrayList<>();

        //Loads in data to adapter from firebase.
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
        //products.add(new Product("Item 1", "This is a desc.", "the seller", 10.5f, "https://firebasestorage.googleapis.com/v0/b/swipeandshop-8168d.appspot.com/o/1619581076067.png?alt=media&token=adaeed50-aab3-4f38-a4b1-e27346bf52cd","myid"));
        //products.add(new Product("Item 1", "This is a desc.", "the seller", 10.5f, "https://i.imgur.com/UYrdDFI.jpeg","myid"));
        return products;
    }

    /**Retrieves data from firebase when page is first loaded.*/
    public void loadData(){
        //Get viewed products by a person so they dont view them again.

        //Also randomize the data order.
        // this will load data from firebase and put it into the products list.
        allProductRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item_snapshot:snapshot.getChildren()){
                    System.out.println(item_snapshot.getValue().toString());
                    Product tempProduct = item_snapshot.getValue(Product.class);
                    products.add(tempProduct);
                }
                setAdapterInfo();
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
                Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + " d=" + direction);
                if (direction == Direction.Right){
                    Toast.makeText(FeedPage.this, "Direction Right", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Top){
                    Toast.makeText(FeedPage.this, "Direction Top", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Left){
                    Toast.makeText(FeedPage.this, "Direction Left", Toast.LENGTH_SHORT).show();
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
                TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", nama: " + tv.getText());
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", nama: " + tv.getText());
            }
        });
        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.FREEDOM);
        manager.setCanScrollHorizontal(true);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        adapter = new ProductCardStackAdapter(addList());
        System.out.println(products.size() + " my siize 2");
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
    }


}