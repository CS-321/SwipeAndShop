package com.example.swipeandshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class ProductPage extends AppCompatActivity {
    RecyclerView productCardList;
    List<Product> products;
    ProductAdapter adapter;
    Button addProductBtn;
    Button cancelProductBtn;
    Button saveProductBtn;
    CardView createProductView;

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

        productCardList = findViewById(R.id.dataList); //holds all the users products
        addProductBtn = findViewById(R.id.addProductBtn);
        products = new ArrayList<>();

        // Create Product View Fields
        cancelProductBtn = findViewById(R.id.cancelButton);
        saveProductBtn = findViewById(R.id.saveButton);
        createProductView = findViewById(R.id.createProductView);
        createProductView.setVisibility(View.GONE);

        adapter = new ProductAdapter(this,products);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        productCardList.setLayoutManager(gridLayoutManager);
        productCardList.setAdapter(adapter);

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

    public void addOnClick(){
        createProductView.setVisibility(View.VISIBLE);
        EditText name = createProductView.findViewById(R.id.nameInput);
        EditText desc = createProductView.findViewById(R.id.descriptionInput);
        name.setText("");//clear text boxes
        desc.setText("");
        addProductBtn.setVisibility(View.GONE);
    }

    public void cancelOnClick(){
        createProductView.setVisibility(View.GONE);
        addProductBtn.setVisibility(View.VISIBLE);
    }

    public void saveOnClick(){
        Product product;
        EditText name = createProductView.findViewById(R.id.nameInput);
        EditText desc = createProductView.findViewById(R.id.descriptionInput); //LET IN 
        int currentLocation = adapter.getProductLocation();
        product = new Product(name.getText().toString(),desc.getText().toString(), "Javi", 10.90f, R.drawable.ic_launcher_background, products.size());
        if(currentLocation >= 0){
            products.remove(currentLocation);
            products.add(product);
        }else{
            products.add(product); // add our new product to the list
        }

        //if(location != -1)
        //  products.get(currentLocation).ediitHere();
        //else
        productCardList.setAdapter(adapter); // updating the grid view to include the new product
        adapter.setProductLocation(-1);
        createProductView.setVisibility(View.GONE);
        addProductBtn.setVisibility(View.VISIBLE);
    }

    public CardView getCreateProductView(){
        return createProductView;
    }





}