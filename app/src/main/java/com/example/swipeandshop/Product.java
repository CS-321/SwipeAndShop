package com.example.swipeandshop;

public class Product {
    private String name = "";
    private String description = "";
    private String seller = "";
    String productId = "";//private User seller;
    private float price = 0;
    private int image; //private ImageClass image;

    public Product(){

    }

    public Product(String name, String description, String seller, float price, int image, String productId){
        this.name = name;
        this.description = description;
        this.seller = seller;
        this.price = price;
        this.image = image;
        this.productId = productId;
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

    public String getProductId() {
        return productId;
    }
}
