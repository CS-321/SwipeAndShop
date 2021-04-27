package com.example.swipeandshop;

import java.util.List;

import androidx.recyclerview.widget.DiffUtil;

public class ProductCardStackCallback extends DiffUtil.Callback {

    private List<Product> old, newer;

    public ProductCardStackCallback(List<Product> old, List<Product> newer) {
        this.old = old;
        this.newer = newer;
    }

    @Override
    public int getOldListSize() {
        return old.size();
    }

    @Override
    public int getNewListSize() {
        return newer.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return old.get(oldItemPosition).getImage() == newer.get(newItemPosition).getImage();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return old.get(oldItemPosition) == newer.get(newItemPosition);
    }
}