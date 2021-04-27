package com.example.swipeandshop;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ProductViewHolder extends RecyclerView.ViewHolder{
    TextView title;
    ImageView gridIcon;


    // Used to load the products view.
    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.textView);
        gridIcon = itemView.findViewById(R.id.imageView2);
    }

    void bindData(ViewHolderCallback callback){
        Button removeBtn = itemView.findViewById(R.id.removeProductButton);

        itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                callback.itemWasClicked(getAdapterPosition());
            }
        });


        removeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                callback.itemToDelete(getAdapterPosition());
            }
        });
    }
}

interface ViewHolderCallback{
    void itemWasClicked(int position);
    void itemToDelete(int position);
}
