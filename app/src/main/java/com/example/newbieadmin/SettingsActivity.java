package com.example.newbieadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newbieadmin.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.net.URI;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private CircleImageView profileImageview;
    private EditText fullnameedittext,userphoneedittext,addressedittext;
    private TextView profilechangetextbutton,closetextbutton,savetextbutton;

    private Uri imageUri;
    private String myUri="";
    StorageTask uploadTask;
    private StorageReference storageprofilepictureRef;
    private String checker="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        profileImageview = findViewById(R.id.settings_profile_image);
        fullnameedittext = findViewById(R.id.settings_full_name);
        userphoneedittext = findViewById(R.id.settings_phone_number);
        addressedittext = findViewById(R.id.settings_address);
        profilechangetextbutton = findViewById(R.id.Profile_image_change_btn);
        closetextbutton = findViewById(R.id.close_settings_btn);
        savetextbutton = findViewById(R.id.update_account_settings_btn);
        storageprofilepictureRef = FirebaseStorage.getInstance().getReference().child("Profile Pictures");

        userInfoDisplay(profileImageview,fullnameedittext,userphoneedittext,addressedittext);

        closetextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        savetextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checker.equals("clicked"))
                {
                    UserInfoSaved();
                }
                else
                    {
                        UpdateOnlyUserInfo();
                    }
            }
        });

        profilechangetextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker="clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });

    }

    private void UpdateOnlyUserInfo()
    {
        DatabaseReference Ref=FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String,Object> userMap = new HashMap<>();
        userMap.put("username",fullnameedittext.getText().toString());
        userMap.put("address",addressedittext.getText().toString());
        userMap.put("phoneOrder",userphoneedittext.getText().toString());

        Ref.child(Prevalent.currentOnlineUser.getPhoneno()).updateChildren(userMap);

        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        Toast.makeText(getApplicationContext(),"User Info Updated",Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null)
        {
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            imageUri= result.getUri();

            profileImageview.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Error,Try Again.....",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));
            finish();
        }
    }

    private void UserInfoSaved()
    {
        if(TextUtils.isEmpty(fullnameedittext.getText().toString())){
            Toast.makeText(getApplicationContext(),"Name is Mandatory",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(addressedittext.getText().toString())){
            Toast.makeText(getApplicationContext(),"Address is Mandatory",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userphoneedittext.getText().toString())){
            Toast.makeText(getApplicationContext(),"phone no. is Mandatory",Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("clicked"))
        {
            uploadImage();
        }

    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait while we are updating profile information...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageUri!=null){
            final StorageReference fileRef = storageprofilepictureRef.child(Prevalent.currentOnlineUser.getPhoneno() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadurl =task.getResult();
                        myUri = downloadurl.toString();

                        DatabaseReference Ref=FirebaseDatabase.getInstance().getReference().child("Users");

                        HashMap<String,Object> userMap = new HashMap<>();
                        userMap.put("username",fullnameedittext.getText().toString());
                        userMap.put("address",addressedittext.getText().toString());
                        userMap.put("phoneOrder",userphoneedittext.getText().toString());
                        userMap.put("image",myUri);

                        Ref.child(Prevalent.currentOnlineUser.getPhoneno()).updateChildren(userMap);

                        progressDialog.dismiss();
                        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        Toast.makeText(getApplicationContext(),"User Info Updated",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }
        else
            {
                Toast.makeText(getApplicationContext(),"Image is Not selected",Toast.LENGTH_SHORT).show();
            }


    }

    private void userInfoDisplay(final CircleImageView profileImageview, final EditText fullnameedittext, final EditText userphoneedittext, final EditText addressedittext)
    {
        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhoneno());
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.child("image").exists())
                    {
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("email").getValue().toString();
                        String phone = dataSnapshot.child("phoneno").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImageview);
                        fullnameedittext.setText(name);
                        userphoneedittext.setText(phone);
                        addressedittext.setText(address);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
