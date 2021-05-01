package com.example.swipeandshop;

public class Product {
    private String name = "";
    private String seller = "";
    String productId = "";
    private float price = 0;
    private String imageUrl = "";

    //new updates.
    private String shortDescription = "";
    private String longDescription = "";
    private String sellerId = "";
    private String imagePath = "";

    public Product(){

    }

    public Product(String name, String description, String seller, float price, String imageUrl, String productId){
        this.name = name;
        this.seller = seller;
        this.price = price;
        this.imageUrl = imageUrl;
        this.productId = productId;
    }

    public String getName(){
        return name;
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

    public String getSellerId() {
        return sellerId;
    }

    public String getProductId() {
        return productId;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
