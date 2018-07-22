package com.example.niket.chatapplication;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.niket.chatapplication.HelperClassForImageBrowseFromGalary_Camera.SelectImageHelper;
import com.example.niket.chatapplication.databinding.ActivityRegisterBinding;
import com.example.niket.chatapplication.pojoClass.MyPojo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Random;

public class Register extends AppCompatActivity {

    ActivityRegisterBinding activityRegisterBinding;
    ProgressDialog dialog;
    SelectImageHelper helper;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    StorageReference storageReference;

    Random random;

    MyPojo myPojo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        activityRegisterBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        //get FirebaseDatabase Instance
        firebaseDatabase = FirebaseDatabase.getInstance();

        //get selectHelper class instance
        helper = new SelectImageHelper(this, activityRegisterBinding.circular);

        //get StorageReference Instance
        storageReference = FirebaseStorage.getInstance().getReference();

        //dialog
        dialog = new ProgressDialog(this);
        dialog.setTitle("please wait.....");
        dialog.setMessage("registering data to Database");
        dialog.setCanceledOnTouchOutside(false);

        random = new Random();
        final String s1 = String.valueOf(random.nextInt(263443));
        /*final String s2=String.valueOf(random.nextInt(26));
        final String s3=String.valueOf(random.nextInt(26));
        final String s4=String.valueOf(random.nextInt(26));
*/
        activityRegisterBinding.circular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //using SelectImageHelper class to browse image
                helper.selectImageOption();
            }
        });


        //making edittext cli
        activityRegisterBinding.editTextDOB.setFocusable(false);
        activityRegisterBinding.editTextDOB.setClickable(true);
        activityRegisterBinding.editTextDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cal = Calendar.getInstance();
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Register.this, android.R.style.Theme_DeviceDefault_Dialog_Alert, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        activityRegisterBinding.editTextDOB.setText(day + "/" + (month + 1) + "/" + year);

                    }
                }, year, month, day);
                datePickerDialog.show();
            }

        });


        //register new user
        activityRegisterBinding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.show();

                //get the DatabaseReference
                databaseReference = firebaseDatabase.getReference("data").child("users");

                Uri file = helper.getURI_FOR_SELECTED_IMAGE();

                StorageReference storageReference2 = storageReference.child(s1);

                storageReference2.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        myPojo = new MyPojo();

                        myPojo.setID(databaseReference.push().getKey());
                        myPojo.setImage_URL(downloadUrl.toString());
                        myPojo.setName(activityRegisterBinding.editTextName.getText().toString());
                        myPojo.setDOB(activityRegisterBinding.editTextDOB.getText().toString());
                        myPojo.setMobile(activityRegisterBinding.editTextMobile.getText().toString());
                        myPojo.setEmail(activityRegisterBinding.editTextEmail.getText().toString());
                        myPojo.setPassword(activityRegisterBinding.editTextPassword.getText().toString());
                        myPojo.setName(activityRegisterBinding.editTextName.getText().toString());


                        databaseReference.child(activityRegisterBinding.editTextName.getText().toString()).setValue(myPojo);

                        dialog.cancel();

                        Intent intent = new Intent(Register.this, Login.class);
                        intent.putExtra("Profile_image", myPojo.getImage_URL());
                        //intent.putExtra("username",activityRegisterBinding.editTextName.getText().toString());
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, "Database Error", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        helper.handleResult(requestCode, resultCode, result);  // call this helper class method
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final @NonNull String[] permissions, final @NonNull int[] grantResults) {
        helper.handleGrantedPermisson(requestCode, grantResults);   // call this helper class method
    }

}
