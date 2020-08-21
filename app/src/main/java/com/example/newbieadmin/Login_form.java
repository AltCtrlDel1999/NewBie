package com.example.newbieadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newbieadmin.Model.Users;
import com.example.newbieadmin.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class Login_form extends AppCompatActivity {
    EditText username,password,phone;
    Button register,login;
    TextView forgetpassword,iamadmin,iamnotadmin;
    CheckBox rememberme;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog loadingBar;
    String parentName="Users";
    String phoneV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_form);

        getSupportActionBar().setTitle("Login Form");
        username=findViewById(R.id.lfetusername);
        password=findViewById(R.id.lfetpassword);
        phone=findViewById(R.id.lfetphone);
        register=findViewById(R.id.lfbregister);
        login=findViewById(R.id.lfblogin);
        rememberme=findViewById(R.id.remember_me_chkb);
        forgetpassword=findViewById(R.id.forget_password_link);
        iamadmin=findViewById(R.id.i_am_admin);
        iamnotadmin=findViewById(R.id.i_am_not_admin);
        firebaseAuth=FirebaseAuth.getInstance();
        loadingBar =new ProgressDialog(this);
        Paper.init(this);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login_form.this,Signup_form.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String usernameV,passswordV;
                usernameV=username.getText().toString().trim();
                passswordV=password.getText().toString().trim();
               // phoneV=phone.getText().toString().trim();



                if(TextUtils.isEmpty(usernameV))
                {
                    Toast.makeText(Login_form.this,"enter username",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(passswordV))
                {
                    Toast.makeText(Login_form.this,"enter password",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (passswordV.length()<6)
                {
                    Toast.makeText(Login_form.this,"password too short",Toast.LENGTH_SHORT).show();

                }
                DatabaseReference phoneRef;
                phoneRef = FirebaseDatabase.getInstance().getReference().child(parentName);

                phoneRef.orderByChild("email").equalTo(usernameV).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot a:dataSnapshot.getChildren()){
                            if(a.getKey()!=null){
                                phoneV = a.getKey();
                                phone.setText(phoneV);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                firebaseAuth.signInWithEmailAndPassword(usernameV, passswordV)
                        .addOnCompleteListener(Login_form.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //startActivity(new Intent(Login_form.this,MainActivity.class));
                                    //Toast.makeText(Login_form.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    //checkEmailVerification(usernameV,passswordV);
                                    phoneV=phone.getText().toString();
                                    if(TextUtils.isEmpty(phoneV))
                                    {
                                        phoneV="123";
                                        phone.setText(phoneV);
                                    }
                                    AllowAccessToUser(usernameV,passswordV,phoneV);

                                } else {
                                    Toast.makeText(Login_form.this, "Login Failed", Toast.LENGTH_SHORT).show();

                                }

                                // ...
                            }
                        });
            }
        });
        iamadmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setText("Login Admin");
                iamadmin.setVisibility(View.INVISIBLE);
                iamnotadmin.setVisibility(View.VISIBLE);
                parentName="Admins";
            }
        });
        iamnotadmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setText("Login");
                iamadmin.setVisibility(View.VISIBLE);
                iamnotadmin.setVisibility(View.INVISIBLE);
                parentName="Users";
            }
        });
        String UPK=Paper.book().read(Prevalent.UserPhoneKey);
        String UPaK=Paper.book().read(Prevalent.UserPasswordKey);
        if(UPK!="" && UPaK!="")
        {
            if(!TextUtils.isEmpty(UPK)&&!TextUtils.isEmpty(UPaK))
            {
                AllowAccess(UPK,UPaK);
            }
        }

    }

    private void AllowAccess(String upk, String uPaK) {
        username.setText(upk);
        password.setText(uPaK);
    }
    private void AllowAccessToUser(final String usernameV, final String passswordV, final String phoneV)
    {
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(parentName.equals("Users"))
                {
                    if(dataSnapshot.child("Users").child(phoneV).exists())
                    {
                        Users userData=dataSnapshot.child(parentName).child(phoneV).getValue(Users.class);
                        if(userData.getPhoneno().equals(phone.getText().toString()))
                        {
                            if(userData.getPassword().equals(passswordV))
                            {
                                Prevalent.currentOnlineUser=userData;
                                checkEmailVerification(usernameV,passswordV,parentName);

                            }
                            else
                            {
                                Toast.makeText(Login_form.this, "Password incorrect", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(Login_form.this, "create new Account", Toast.LENGTH_SHORT).show();

                    }
                }
                if(parentName.equals("Admins"))
                {
                    if(dataSnapshot.child("Admins").child(phoneV).exists())
                    {
                        Users userData=dataSnapshot.child(parentName).child(phoneV).getValue(Users.class);
                        if(userData.getPhoneno().equals(phone.getText().toString()))
                        {
                            if(userData.getPassword().equals(passswordV))
                            {
                                Prevalent.currentOnlineUser=userData;
                                checkEmailVerification(usernameV,passswordV,parentName);

                            }
                            else
                            {
                                Toast.makeText(Login_form.this, "Password incorrect", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(Login_form.this, "create new Account", Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void checkEmailVerification(String username,String password,String parentName)
    {
        FirebaseUser firebaseUser=firebaseAuth.getInstance().getCurrentUser();
        Boolean emailFlag=firebaseUser.isEmailVerified();
        if(emailFlag)
        {
            if(rememberme.isChecked())
            {
                Paper.book().write(Prevalent.UserPhoneKey,username);
                Paper.book().write(Prevalent.UserPasswordKey,password);

            }
            loadingBar.setTitle("Login");
            loadingBar.setMessage("Please wait while we are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            if(parentName.equals("Users")) {
                finish();

                startActivity(new Intent(Login_form.this, HomeActivity.class));
                loadingBar.dismiss();
                Toast.makeText(Login_form.this, "Login Successful", Toast.LENGTH_SHORT).show();
            }
            else
            {
                if(parentName.equals("Admins")) {
                    finish();
                    startActivity(new Intent(Login_form.this, AdminaddcategoryActivity.class));
                    loadingBar.dismiss();
                    Toast.makeText(Login_form.this, "Login Successful", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else
        {
            Toast.makeText(Login_form.this, "Verify Your Email", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();

        }
    }
}
