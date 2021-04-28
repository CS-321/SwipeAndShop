package com.example.swipeandshop;

public class Product {
    private String name = "";
    private String description = "";
    private String seller = "";
    String productId = "";//private User seller;
    private float price = 0;
    private String imageUrl; //private ImageClass image;

    public Product(){

    }

    public Product(String name, String description, String seller, float price, String imageUrl, String productId){
        this.name = name;
        this.description = description;
        this.seller = seller;
        this.price = price;
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public String getProductId() {
        return productId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
