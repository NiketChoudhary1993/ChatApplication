package com.example.niket.chatapplication;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.niket.chatapplication.Adapter.ChatAdapter;
import com.example.niket.chatapplication.HelperClassForImageBrowseFromGalary_Camera.AttachFileHelper;
import com.example.niket.chatapplication.HelperClassForImageBrowseFromGalary_Camera.SelectImageHelper;
import com.example.niket.chatapplication.pojoClass.ImagePojo;
import com.example.niket.chatapplication.pojoClass.MessagePojo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconsPopup;
import hani.momanii.supernova_emoji_library.emoji.Emojicon;


public class IndividualChatPage extends AppCompatActivity {

    CircleImageView circleImageView;
    Dialog dialog;

    ImageView attachImageView;

    SelectImageHelper helper;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceToAddChat, databaseReferenceToshowChat;


    Random random;

    String image;
    ImageView backButton;

    DatabaseReference databaseReference, databaseReference2;

    TextView textView;
    CircleImageView send;

    ImageView emoji, attach;
    EmojiconEditText message;
    View rootView;
    EmojIconActions emojIcon;

    StorageReference storageReference;
    String receiverID, senderID, name, senderName;
    MessagePojo messagePojo;
    ArrayList<MessagePojo> messagePojoArrayList = new ArrayList<>();

    ChatAdapter chatAdapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    String messageKey, messageKey2;
    ArrayList<String> arrayList = new ArrayList<>();

    String userNameMessagePushKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_chat_page);
        rootView = findViewById(R.id.linear);


        attachImageView = findViewById(R.id.attachFile);
        helper = new SelectImageHelper(this, attachImageView);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        circleImageView = findViewById(R.id.profileimage);
        textView = findViewById(R.id.name);


        send = findViewById(R.id.send);
        //set send button set as enabled=false
        send.setEnabled(false);

        message = findViewById(R.id.editMessage);
        recyclerView = findViewById(R.id.recyclerview_message);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        random = new Random();
        final String s1 = String.valueOf(random.nextInt(263443));
        setSupportActionBar(toolbar);


        SharedPreferences sharedPreferences = getSharedPreferences("save", 0);
        senderID = sharedPreferences.getString("senderID", null);
        senderName = sharedPreferences.getString("senderName", null);
        Log.d("sender", "onCreate: " + senderName);

        Intent i = getIntent();
        name = i.getStringExtra("name");
        image = i.getStringExtra("image");
        String mobile = i.getStringExtra("number");
        receiverID = i.getStringExtra("ReceiverID");


        firebaseDatabase = FirebaseDatabase.getInstance();

        //get StorageReference Instance
        storageReference = FirebaseStorage.getInstance().getReference();

        //attach file
        attach = findViewById(R.id.attachFile);

        //Emoji keyboard
        emoji = findViewById(R.id.emoji);
        emojIcon = new EmojIconActions(IndividualChatPage.this, rootView, message, emoji, "#F44336", "#e8e8e8", "#f4f4f4");
        emojIcon.ShowEmojIcon();


        backButton = findViewById(R.id.backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        attachImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView profile_dialog;

                //  emojIcon.closeEmojIcon();
                dialog = new Dialog(IndividualChatPage.this);
                dialog.setContentView(R.layout.dialog_profile_image);

                profile_dialog = dialog.findViewById(R.id.profile_dialog);

                Glide.with(IndividualChatPage.this).load(image).diskCacheStrategy(DiskCacheStrategy.ALL).into(profile_dialog);

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

            }
        });

        //  firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceToAddChat = firebaseDatabase.getReference("data").child("chats");

        Log.d("1", "sender" + name);
        // databaseReferenceToshowChat = firebaseDatabase.getReference("data").child("users").child("sender");


        Glide.with(this).load(image).diskCacheStrategy(DiskCacheStrategy.ALL).into(circleImageView);
        textView.setText(name);


        //check if edittext is empty or not ,if empty keep send button as non clickable
        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().length() == -1) {
                    send.setEnabled(false);
                } else {
                    send.setEnabled(true);
                }

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //checking wheter text send to firebase is empty
                if (!TextUtils.isEmpty(message.getText().toString())) {

                    messagePojo = new MessagePojo();
                    messagePojo.setSenderID(senderID);
                    messagePojo.setReceiverID(receiverID);
                    /*messageKey2 = databaseReferenceToAddChat.push().getKey();
                    Log.d("12345", "onDataChange2: " + messageKey2.toString());*/
                    messagePojo.setMessageId(databaseReferenceToAddChat.push().getKey());
                    messagePojo.setMessage(message.getText().toString());
                    messagePojo.setReceiverName(name);
                    messagePojo.setSenderName(senderName);

                    databaseReferenceToAddChat.child(messagePojo.getMessageId()).setValue(messagePojo);

                   /* databaseReferenceToAddChat.child("sender").child(senderID).child(messagePojo.getMessageId()).setValue(messagePojo);
                    databaseReferenceToAddChat.child("receiver").child(receiverID).child(messagePojo.getMessageId()).setValue(messagePojo);
*/
                    message.setText(null);
                    send.setEnabled(false);

                }

            }


        });

        databaseReferenceToAddChat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                messagePojoArrayList.clear();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    messagePojo = data.getValue(MessagePojo.class);

                    messageKey = data.getKey();
                    arrayList.add(messageKey);

                    Log.d("12345", "onDataChange: " + messageKey.toString());
                    messagePojoArrayList.add(messagePojo);
                }

                chatAdapter = new ChatAdapter(IndividualChatPage.this, messagePojoArrayList, senderID, receiverID, arrayList, name, senderName);
                recyclerView.setAdapter(chatAdapter);

                //to set the postion of recyclerview to last
                if (messagePojoArrayList != null && !messagePojoArrayList.isEmpty()) {
                    recyclerView.scrollToPosition(messagePojoArrayList.size() - 1);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


}
