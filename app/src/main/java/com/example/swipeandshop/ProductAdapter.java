package com.example.swipeandshop;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    List<Product> products;
    LayoutInflater inflater;
    Activity productActivity;
    private int productLocation;
    public ProductAdapter(Context ctx, List<Product> products){
        this.products = products;
        this.inflater = LayoutInflater.from(ctx);
        productActivity = (Activity)ctx;
        productLocation = -1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_product_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.title.setText(product.getName());
        holder.gridIcon.setImageResource(product.getImage());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView gridIcon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textView);
            gridIcon = itemView.findViewById(R.id.imageView2);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int location = getAdapterPosition();
                    Toast.makeText(v.getContext(), "Clicked -> " + location, Toast.LENGTH_SHORT).show();
                    // how to open up the create product view.
                    CardView createProductView = productActivity.findViewById(R.id.createProductView);
                    createProductView.setVisibility(View.VISIBLE);
                    productLocation = getAdapterPosition();
                    populateCreateProductView(createProductView, getAdapterPosition());

                }
            });
        }

        public void populateCreateProductView(CardView view, int productIndex){
            EditText name = (EditText) view.findViewById(R.id.nameInput);
            EditText description = (EditText) view.findViewById(R.id.descriptionInput);
            //EditText price = view.findViewById(R.id.priceInput);
            Product product = products.get(productIndex); //get product at index
            name.setText(product.getName());
            description.setText(product.getDescription());
            //price.set
        }
    }

    public int getProductLocation(){
        return productLocation;
    }

    public void setProductLocation(int loc){
        productLocation = loc;
    }
}


