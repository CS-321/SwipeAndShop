package com.example.swipeandshop;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatViewHolder extends RecyclerView.ViewHolder{
    TextView name;

    // Used to load the products view.
    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.chatName);
    }

    void bindData(ViewHolderCallback callback){

        itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                callback.itemWasClicked(getAdapterPosition());
            }
        });
    }
}
