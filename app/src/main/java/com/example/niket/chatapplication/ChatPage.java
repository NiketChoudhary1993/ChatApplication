package com.example.niket.chatapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.niket.chatapplication.Adapter.MyCustomAdapter;
import com.example.niket.chatapplication.pojoClass.MyPojo;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class ChatPage extends AppCompatActivity  {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    MyCustomAdapter customAdapter;
    ArrayList<MyPojo> arrayList = new ArrayList<>();

    String senderId;
    SharedPreferences sharedPreferences;
    MyPojo userPojo;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;

    FirebaseAuth firebaseAuth;

    FirebaseUser user;

    String message;
    ArrayList<String> arrayListForDelete=new ArrayList<>();

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    //for back pressed button
    boolean isUserClickedBackButton = false;

    ItemTouchHelper itemTouchHelper;

    String senderName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);


        recyclerView = findViewById(R.id.recyclerview);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);


        sharedPreferences = getSharedPreferences("save", 0);
        senderId = sharedPreferences.getString("senderID", null);


        firebaseDatabase = FirebaseDatabase.getInstance();

       // reference = firebaseDatabase.getReference("Details").child("accounts");
        reference = firebaseDatabase.getReference("data").child("users");
        getFirebaseData(reference);

        firebaseAuth = FirebaseAuth.getInstance();

       /* ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                DatabaseReference deleteItem=FirebaseDatabase.getInstance().getReference("Details").child(senderId);
                deleteItem.removeValue();
                Toast.makeText(ChatPage.this, deleteItem.toString(), Toast.LENGTH_SHORT).show();



            }
        };

        itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
      //  itemTouchHelper.notifyAll();
        itemTouchHelper.attachToRecyclerView(recyclerView);*/
    }

    private void getFirebaseData(final DatabaseReference reference) {

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("123", "datasnap: "+dataSnapshot);
                arrayList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    Log.d("123", "data2: ");
                    message=data.getKey();
                    arrayListForDelete.add(message);

                    userPojo = data.getValue(MyPojo.class);
                    arrayList.add(userPojo);
                }



                customAdapter = new MyCustomAdapter(ChatPage.this, arrayList, senderId,arrayListForDelete);
                recyclerView.setAdapter(customAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChatPage.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case R.id.logout_menu:
                user=firebaseAuth.getCurrentUser();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();

                Intent intent = new Intent(ChatPage.this, Login.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (!isUserClickedBackButton) {
            Toast.makeText(this, "back disabled", Toast.LENGTH_SHORT).show();
            //isUserClickedBackButton=true;
        } else {
            super.onBackPressed();
            System.exit(0);
        }
        new CountDownTimer(1, 1000) {

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish()
            {

                isUserClickedBackButton = false;
            }
        }.start();
    }
}
