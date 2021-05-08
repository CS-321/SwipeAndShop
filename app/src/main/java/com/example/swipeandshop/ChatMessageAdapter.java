package com.example.swipeandshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {
    public ChatMessageAdapter(@NonNull Context context, ArrayList<ChatMessage> messages) {
        super(context,0, messages);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ChatMessage msg = getItem(position);
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (convertView == null) {
            //convertView = LayoutInflater.from(getContext()).inflate(R.layout.message, parent, false);
                if(msg.getUser().equals(user.getEmail())){
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_blue, parent, false);
                }else{
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.message, parent, false);
                }

        }

        TextView msgContent = convertView.findViewById(R.id.messageTextView);
        TextView msgUser = convertView.findViewById(R.id.messengerTextView);

        if(msg != null){
            msgContent.setText(msg.getMessage());
            msgUser.setText(msg.getUser());
        }


        return convertView;
    }
}
