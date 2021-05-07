package com.example.swipeandshop;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatMessageViewHolder extends RecyclerView.ViewHolder{
    TextView message;
    TextView userName;

    // Used to load the products view.
    public ChatMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        message= itemView.findViewById(R.id.textView);
        userName= itemView.findViewById(R.id.textView);
    }

    void bindData(ViewHolderCallback callback){
        Button removeBtn = itemView.findViewById(R.id.removeProductButton);

        itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                //callback.itemWasClicked(getAdapterPosition());
            }
        });
    }
}
