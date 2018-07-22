package com.example.niket.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.niket.chatapplication.Adapter.MyCustomAdapter;
import com.example.niket.chatapplication.databinding.ActivityLoginBinding;
import com.example.niket.chatapplication.pojoClass.MyPojo;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class Login extends AppCompatActivity {

    ActivityLoginBinding activityLoginBinding;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, databaseReference_google;
    SharedPreferences sharedPreferences;

    MyCustomAdapter customAdapter;
    MyPojo myPojo, myPojo_google;
    ProgressDialog dialog, dialog_integration;

    //initialising GoogleSignInOption and GoogleSignInClient
    GoogleSignInClient googleSignInClient;
    GoogleSignInOptions googleSignInOptions;

    //firebase Auth
    FirebaseAuth firebaseAuth;

    FirebaseUser user;
String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        //getting userName from register activity
       /* Intent user=getIntent();
        userName=user.getStringExtra("username");
        Log.d("12345", "login: "+userName);*/

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
       // databaseReference = firebaseDatabase.getReference("Details").child("accounts");
        databaseReference = firebaseDatabase.getReference("data").child("users");
        dialog = new ProgressDialog(this);
        dialog.setTitle("please wait....");
        dialog.setMessage("verifying user");
        dialog.setCanceledOnTouchOutside(false);


        //integration dialog
        dialog_integration = new ProgressDialog(this);
        dialog_integration.setTitle("please wait....");
        dialog_integration.setMessage("verifying account");
        dialog_integration.setCanceledOnTouchOutside(false);


        sharedPreferences = getSharedPreferences("save", 0);


        if (sharedPreferences.getBoolean("status", false)) {

            Intent intent = new Intent(Login.this, ChatPage.class);
            startActivity(intent);
        }

        activityLoginBinding.googleSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //initialising GoogleSignInOption
                googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                //initialising googleSigninClient
                googleSignInClient = GoogleSignIn.getClient(Login.this, googleSignInOptions);


                //Implicit Intent for googleSignIn
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 100);

                //ask email each time when user click to signup with google
                googleSignInClient.signOut();


            }
        });


        activityLoginBinding.Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        activityLoginBinding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.show();

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        boolean userFound = false;

                        for (DataSnapshot data : dataSnapshot.getChildren()) {

                            myPojo = data.getValue(MyPojo.class);

                            if (activityLoginBinding.Mobile.getText().toString().equals(myPojo.getMobile())
                                    && activityLoginBinding.editTextPassword.getText().toString().equals(myPojo.getPassword())) {

                                dialog.cancel();

                                userFound = true;

                                break;
                            }
                        }

                        if (userFound == true) {

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("status", true);
                            editor.putString("senderID", myPojo.getID());
                            editor.putString("senderName",myPojo.getName());
                            Log.d("register", "register: "+myPojo.getName());
                            editor.commit();

                            Intent intent = new Intent(Login.this, ChatPage.class);
                            startActivity(intent);

                        } else {

                            dialog.cancel();
                            Toast.makeText(Login.this, "invalid Credentials", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(Login.this, "Database error", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            //display dialog box
            dialog_integration.show();

            //database reference
            databaseReference_google = firebaseDatabase.getReference("data").child("users");

            //intantiating new object of mypojo class
            MyPojo myPojo1 = new MyPojo();

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task, myPojo1, databaseReference_google);

        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> task, MyPojo myPojo1, DatabaseReference databaseReference_google) {

        try {

            final GoogleSignInAccount account = task.getResult(ApiException.class);

            firebaseAuthWithGoogle(account, myPojo1, databaseReference_google);


        } catch (ApiException e) {
            Toast.makeText(Login.this, "signInResult:failed code=" + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            Log.d("12345", "signInResult:failed code=" + e.getStatusCode());


        }

    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account, final MyPojo myPojo1, final DatabaseReference databaseReference_google) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    //add value to MyPojo class from google account

                     user = firebaseAuth.getCurrentUser();


                    myPojo1.setImage_URL(user.getPhotoUrl().toString());
                    myPojo1.setName(user.getDisplayName());
                    myPojo1.setID(user.getUid());
                    myPojo1.setMobile(user.getPhoneNumber());
                    myPojo1.setEmail(user.getEmail());

                    databaseReference_google.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            boolean exists = false;
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                Map<String, Object> model = (Map<String, Object>) child.getValue();

                                if(model.get("id").equals(myPojo1.getID())) {
                                    exists = true;
                                    break;
                                }
                            }
                            if(exists) {
                                Toast.makeText(Login.this, "welcome back: "+user.getDisplayName(), Toast.LENGTH_SHORT).show();

                                dialog_integration.cancel();

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("status", true);
                                editor.putString("senderID", myPojo1.getID());
                                //editor.putString("senderName",myPojo1.getName());
                                editor.commit();
                                Intent intent = new Intent(Login.this, ChatPage.class);
                                startActivity(intent);
                                //exists=false;
                            }
                            else {

                                // This user doesn't exists in firebase.
                                databaseReference_google.push().setValue(myPojo1);

                                Toast.makeText(Login.this, "welcome : " + user.getDisplayName(), Toast.LENGTH_SHORT).show();

                                dialog_integration.cancel();

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("status", true);
                                editor.putString("senderID", myPojo1.getID());
                                editor.commit();
                                Intent intent = new Intent(Login.this, ChatPage.class);
                                startActivity(intent);

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }


                    });

                } else {
                    // If sign in fails, display a message to the user.

                    Toast.makeText(Login.this, "Authentication failed", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

}
