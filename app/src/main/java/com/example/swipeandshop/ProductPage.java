package com.example.swipeandshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class ProductPage extends AppCompatActivity {
    RecyclerView dataList;
    List<String> titles;
    List<Integer> images;
    ProductAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);
        dataList = findViewById(R.id.dataList);

        titles = new ArrayList<>();
        images = new ArrayList<>();

        titles.add("First Item");
        titles.add("Second Item");
        titles.add("Second Item");
        titles.add("Second Item");
        titles.add("Second Item");
        titles.add("Second Item");
        titles.add("Second Item");
        titles.add("Second Item");
        titles.add("Second Item");
        titles.add("Second Item");
        titles.add("Second Item");




        images.add(R.drawable.ic_launcher_background);
        images.add(R.drawable.ic_airplanemode_active_black_24dp);
        images.add(R.drawable.ic_airplanemode_active_black_24dp);
        images.add(R.drawable.ic_airplanemode_active_black_24dp);
        images.add(R.drawable.ic_airplanemode_active_black_24dp);
        images.add(R.drawable.ic_airplanemode_active_black_24dp);
        images.add(R.drawable.ic_airplanemode_active_black_24dp);
        images.add(R.drawable.ic_airplanemode_active_black_24dp);
        images.add(R.drawable.ic_airplanemode_active_black_24dp);
        images.add(R.drawable.ic_airplanemode_active_black_24dp);
        images.add(R.drawable.ic_airplanemode_active_black_24dp);


        adapter = new ProductAdapter(this,titles,images);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        dataList.setLayoutManager(gridLayoutManager);
        dataList.setAdapter(adapter);
    }
}