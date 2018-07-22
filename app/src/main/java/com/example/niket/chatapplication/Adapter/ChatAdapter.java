package com.example.niket.chatapplication.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.niket.chatapplication.IndividualChatPage;
import com.example.niket.chatapplication.R;
import com.example.niket.chatapplication.pojoClass.MessagePojo;
import com.example.niket.chatapplication.pojoClass.MyPojo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;


/**
 * Created by Niket on 2/2/2018.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ContactViewHolder> {
    private ArrayList<MessagePojo> Pojos = new ArrayList<>();
    private Context context;
    MessagePojo messagePojo;
    View view;
    String receiverID, senderID;
    String messageKey;
    ArrayList<String> arrayList;
    String name;
    String senderName;
    String deleteMessageKey;

    public ChatAdapter(Context context, ArrayList<MessagePojo> Pojos, String senderID, String receiverID, ArrayList<String> arrayList, String name, String senderName) {
        this.context = context;
        this.Pojos = Pojos;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.messageKey = messageKey;
        this.arrayList = arrayList;
        this.name = name;
        this.senderName = senderName;

    }


    @Override
    public ChatAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_message, parent, false);
        ChatAdapter.ContactViewHolder contactViewHolder = new ChatAdapter.ContactViewHolder(view);

        return contactViewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ChatAdapter.ContactViewHolder holder, final int position) {
        messagePojo = Pojos.get(position);
        //Log.d("12345", "onBindViewHolder: " + messagePojo.getMessageId());


        if (senderID.equals(messagePojo.getSenderID()) && receiverID.equals(messagePojo.getReceiverID())
                || senderID.equals(messagePojo.getReceiverID()) && receiverID.equals(messagePojo.getSenderID())) {

            if (senderID.equals(messagePojo.getSenderID())) {

                holder.imageView.setVisibility(View.GONE);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                layoutParams.setMargins(10, 10, 10, 10);

                holder.cardView.setLayoutParams(layoutParams);


            } else {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                layoutParams.setMargins(10, 10, 10, 10);
                holder.imageView.setVisibility(View.GONE);
                holder.cardView.setLayoutParams(layoutParams);
                holder.cardView.setCardBackgroundColor(Color.LTGRAY);
            }

            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    messagePojo = Pojos.get(position);

                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.delete);
                    Button b = dialog.findViewById(R.id.delete);
                    dialog.show();

                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            deleteMessageKey = arrayList.get(position);
                            Log.d("123456", "onClick: " + deleteMessageKey);

                            final DatabaseReference delete = FirebaseDatabase.getInstance().getReference("data").child("chats").child(messagePojo.getMessageId());


                            delete.removeValue();
                            arrayList.clear();
                            dialog.dismiss();
                        }
                    });


                    return true;
                }
            });

           // Glide.with(context).load(messagePojo.getImageUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imageView);

            holder.textMessage.setMaxWidth(550);

            holder.textMessage.setText(messagePojo.getMessage());

        } else {

            holder.cardView.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(0, 0);
            holder.cardView.setLayoutParams(layoutParams);

           /* //causing error class cast exception
            holder.cardView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));*/
        }
    }


    @Override
    public int getItemCount() {
        return Pojos.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        EmojiconTextView textMessage;
        ImageView imageView;
        CardView cardView;

        public ContactViewHolder(View itemView) {
            super(itemView);


            imageView = itemView.findViewById(R.id.attachImage);
            cardView = itemView.findViewById(R.id.cv_message);
            textMessage = itemView.findViewById(R.id.text_message);


        }
    }


}


