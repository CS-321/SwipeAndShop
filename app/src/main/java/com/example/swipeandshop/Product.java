package com.example.swipeandshop;

public class Product {
    private String name;
    private String description;
    private String seller; //private User seller;
    private float price;
    private int image; //private ImageClass image;
    private int localId = -1;

    public Product(String name, String description, String seller, float price, int image, int localId){
        this.name = name;
        this.description = description;
        this.seller = seller;
        this.price = price;
        this.image = image;
        this.localId = localId;
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public float getPrice() {
        return price;
    }

    public String getSeller() {
        return seller;
    }

    public int getImage() {
        return image;
    }
}
