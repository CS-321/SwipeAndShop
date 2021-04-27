package com.example.swipeandshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductPage extends AppCompatActivity {
    List<Product> products;
    RecyclerView productCardList;

    //Product page objects.
    Activity productActivity;
    CardView createProductView;
    int currentLocation = -1;

    // Buttons
    Button addProductBtn;
    Button cancelProductBtn;
    Button saveProductBtn;

    // Firebase references.
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    FirebaseRecyclerOptions<Product> options;
    FirebaseRecyclerAdapter<Product, ProductViewHolder> myAdapter;
    DatabaseReference myRef;
    FirebaseUser user;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsPage.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);

        // Product page objects.
        productCardList = findViewById(R.id.dataList); //holds all the users products
        addProductBtn = findViewById(R.id.addProductBtn);
        productActivity = (Activity)this;
        products = new ArrayList<>();


        //Database
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = firebaseAuth.getCurrentUser();
        myRef = database.getReference().child("users").child(user.getUid()).child("products");

        // Create Product View Fields
        cancelProductBtn = findViewById(R.id.cancelButton);
        saveProductBtn = findViewById(R.id.saveButton);
        createProductView = findViewById(R.id.createProductView);
        createProductView.setVisibility(View.GONE);

        options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(myRef,Product.class).build();

        //load data.
        loadData();
        //show data.
        showData();
        //set listeners.
        setButtonListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Start the adapter listener
        myAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Stop the adapter listener
        myAdapter.stopListening();
    }


    /**Open create product view for creating a new product.*/
    public void addOnClick(){
        createProductView.setVisibility(View.VISIBLE);
        EditText name = createProductView.findViewById(R.id.nameInput);
        EditText desc = createProductView.findViewById(R.id.descriptionInput);
        name.setText("");//clear text boxes
        desc.setText("");
        addProductBtn.setVisibility(View.GONE);
    }

    /**Cancel the editing or creation of an object.*/
    public void cancelOnClick(){
        createProductView.setVisibility(View.GONE);
        addProductBtn.setVisibility(View.VISIBLE);
        this.currentLocation = -1;
    }

    /**Save product. Either adds or edits a product.*/
    public void saveOnClick(){
        Product product;
        EditText name = createProductView.findViewById(R.id.nameInput);
        EditText desc = createProductView.findViewById(R.id.descriptionInput); //LET IN


         // updating the grid view to include the new product
        createProductView.setVisibility(View.GONE);
        addProductBtn.setVisibility(View.VISIBLE);

        //Add product to Realtime database
        user = firebaseAuth.getCurrentUser();

        // if currentLocation >= 0 then we are editing an already existing obj.
        // if currentLocation < 0 then create a new object.
        if(currentLocation >= 0){
            product = products.remove(currentLocation);
            product = new Product(name.getText().toString(),desc.getText().toString(), "Javi", 10.90f, R.drawable.ic_launcher_background,product.productId);
            products.add(0,product);
            myRef.child(product.productId).setValue(product);
        }else{
            DatabaseReference newRef = myRef.push();
            product = new Product(name.getText().toString(),desc.getText().toString(), "Javi", 10.90f, R.drawable.ic_launcher_background,newRef.getKey());
            products.add(product); // add our new product to the list
            newRef.setValue(product);
        }

        this.currentLocation = -1;
        productCardList.setAdapter(myAdapter);
    }

    /**Retrieves data from firebase when page is first loaded.*/
    public void loadData(){
        // this will load data from firebase and put it into the products list.
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item_snapshot:snapshot.getChildren()){
                    Product tempProduct = item_snapshot.getValue(Product.class);
                    products.add(tempProduct);
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**Loads data into teh firebaseUI adapter and displays it in the grid layout.*/
    public void showData(){
        // This takes the loaded data from firebase and puts it into the adapter.
        myAdapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull Product product) {
                productViewHolder.title.setText(product.getName());
                //set other info here.

                //Used to get which item is clicked on in the view.
                productViewHolder.bindData(new ViewHolderCallback(){
                    @Override
                    public void itemWasClicked(int position){
                        // populate the text fields for creating/editing a product.
                        populateCreateProductView(createProductView, position);
                    }

                    @Override
                    public void itemToDelete(int position) {
                        // remove product from database and product list
                        removeProduct(position);
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Inflate the view (instantiate the card in the view).
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_product_layout,parent,false);
                return new ProductViewHolder(view);
            }
        };

        // Set grid to our adapter.
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        productCardList.setLayoutManager(gridLayoutManager);
        productCardList.setAdapter(myAdapter);
    }

    /**Fill in the data for the product in each text field if data was already provided.*/
    public void populateCreateProductView(CardView view, int productIndex){
        // Set the create/edit product card visbile
        createProductView.setVisibility(View.VISIBLE);

        // Get references to each text box.
        EditText name = (EditText) view.findViewById(R.id.nameInput);
        EditText description = (EditText) view.findViewById(R.id.descriptionInput);

        // get the product that was clicked on.
        Product product = products.get(productIndex);

        // populate those text boxes.
        name.setText(product.getName());
        description.setText(product.getDescription());

        // set the current location of the product in the products list to be used for later.
        this.currentLocation = productIndex;
    }

    public void removeProduct(int position){
        // removed product from products list and store in a temp var.
        Product tempProduct = products.remove(position);
        // remove product using product id.
        myRef.child(tempProduct.productId).removeValue();
        // update the adapter.
        productCardList.setAdapter(myAdapter);
    }

    /**Set all the button listeners in the view.*/
    public void setButtonListeners(){
        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOnClick();
            }
        });

        cancelProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelOnClick();
            }
        });

        saveProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveOnClick();
            }
        });
    }








}