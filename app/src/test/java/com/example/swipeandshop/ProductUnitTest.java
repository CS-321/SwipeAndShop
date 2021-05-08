package com.example.swipeandshop;

import org.junit.Test;

import static org.junit.Assert.*;

public class ProductUnitTest {
    @Test
    public void name_isCorrect() {
        Product p = new Product();
        p.setName("joe");
        assertEquals("joe", p.getName());
    }

    @Test
    public void seller_isCorrect() {
        Product p = new Product();
        p.setSeller("joe");
        assertEquals("joe", p.getSeller());
        p.setSellerId("12");
        assertEquals("12", p.getSellerId());
    }

    @Test
    public void productid_isCorrect() {
        Product p = new Product();
        p.setProductId("100");
        assertEquals("100", p.getProductId());
    }

    @Test
    public void price_isCorrect() {
        Product p = new Product();
        p.setPrice((float) 14.48);
        assertEquals(14.48, p.getPrice(), 0.001);
    }

    @Test
    public void image_isCorrect() {
        Product p = new Product();
        p.setImageUrl("https://image1");
        assertEquals("https://image1", p.getImageUrl());
        p.setImagePath("img_path");
        assertEquals("img_path", p.getImagePath());
    }

    @Test
    public void description_isCorrect() {
        Product p = new Product();
        p.setShortDescription("shortdescr");
        assertEquals("shortdescr", p.getShortDescription());
        p.setLongDescription("longdescr log long very long");
        assertEquals("longdescr log long very long", p.getLongDescription());
    }
}