package com.example.niket.chatapplication.Adapter;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.niket.chatapplication.ChatPage;
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
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.ContactViewHolder> {

    private ArrayList<MyPojo> pojos = new ArrayList<>();
    private Context context;
    MyPojo myPojo;
    String senderId;
    ChatPage chatPage;
    View view;
    ArrayList<String> arrayListForDelete;
    String messageKey;


    public MyCustomAdapter(Context context, ArrayList<MyPojo> pojos, String senderId, ArrayList<String> arrayListForDelete) {
        this.context = context;
        this.pojos = pojos;
        this.senderId = senderId;
        this.arrayListForDelete = arrayListForDelete;


    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_item, parent, false);
        ContactViewHolder contactViewHolder = new ContactViewHolder(view);

        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(final ContactViewHolder holder, final int position) {
        myPojo = pojos.get(position);


        if (senderId.equals(myPojo.getID())) {
            holder.cardView.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(0, 0);
            holder.cardView.setLayoutParams(layoutParams);

        }


        holder.textName.setText(myPojo.getName());
        holder.textName.setTextSize(20);

        //holder.textNumber.setText(myPojo.getMobile());
        holder.textNumber.setText(myPojo.getEmail());
        Glide.with(context).load(myPojo.getImage_URL()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.profileImage);


        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myPojo = pojos.get(position);

                ImageView profile_dialog;
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.main_profile_dialog);

                profile_dialog = dialog.findViewById(R.id.profile_dialog);

                Glide.with(context).load(myPojo.getImage_URL()).diskCacheStrategy(DiskCacheStrategy.ALL).into(profile_dialog);

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            }
        });

        //  holder.cardView.isLongClickable();
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myPojo = pojos.get(position);

                Intent i = new Intent(context, IndividualChatPage.class);
                i.putExtra("name", myPojo.getName());
                i.putExtra("number", myPojo.getMobile());
                i.putExtra("image", myPojo.getImage_URL());
                i.putExtra("ReceiverID", myPojo.getID());

                context.startActivity(i);
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                myPojo = pojos.get(position);

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.delete);
                Button b = dialog.findViewById(R.id.delete);
                dialog.show();

                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        messageKey = arrayListForDelete.get(position);
                        Log.d("12345", "onClick: " + messageKey);
                        DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference("data").child("users").child(messageKey);
                        DatabaseReference databaseReferenceUserMessage = FirebaseDatabase.getInstance().getReference("data").child("chats").child(myPojo.getName());

                        databaseReferenceUser.removeValue();
                        databaseReferenceUserMessage.removeValue();

                        arrayListForDelete.clear();
                        dialog.dismiss();
                    }
                });


                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return pojos.size();
    }

    public int getItemViewType(int position) {
        return position;
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView textName, textNumber;
        CircleImageView profileImage;
        CardView cardView;

        public ContactViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cv);
            profileImage = itemView.findViewById(R.id.image);

            textName = itemView.findViewById(R.id.textViewName);
            textNumber = itemView.findViewById(R.id.textViewNumber);
        }
    }
}

