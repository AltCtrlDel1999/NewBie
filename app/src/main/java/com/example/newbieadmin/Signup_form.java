package com.example.newbieadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.HashMap;

public class Signup_form extends AppCompatActivity {

    EditText username,password,confirmpassword,phoneno;
    Button register;
    TextView iamadmin,iamnotadmin;
    String parentName="Users";
    private FirebaseAuth firebaseAuth;
    ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_form);
        getSupportActionBar().setTitle("Signup Form");
        username=findViewById(R.id.sfetusername);
        password=findViewById(R.id.sfetpassword);
        confirmpassword=findViewById(R.id.sfetconpassword);
        register=findViewById(R.id.sfbregister);
        phoneno=findViewById(R.id.sfetphoneno);
        iamadmin=findViewById(R.id.i_am_adminsf);
        iamnotadmin=findViewById(R.id.i_am_not_adminsf);
        loadingBar=new ProgressDialog(this);
        firebaseAuth=FirebaseAuth.getInstance();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailV,passwordV,confPassV,phonenoV;
                emailV=username.getText().toString().trim();
                passwordV=password.getText().toString().trim();
                confPassV=confirmpassword.getText().toString().trim();
                phonenoV=phoneno.getText().toString().trim();

                if(TextUtils.isEmpty(emailV))
                {
                    Toast.makeText(Signup_form.this,"enter username",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(passwordV))
                {
                    Toast.makeText(Signup_form.this,"enter password",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(confPassV))
                {
                    Toast.makeText(Signup_form.this,"enter confirm password",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(emailV))
                {
                    Toast.makeText(Signup_form.this,"enter username",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (passwordV.length()<6)
                {
                    Toast.makeText(Signup_form.this,"password too short",Toast.LENGTH_SHORT).show();

                }
                if (passwordV.equals(confPassV)) {

                    firebaseAuth.createUserWithEmailAndPassword(emailV, passwordV)
                            .addOnCompleteListener(Signup_form.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        loadingBar.setTitle("Create Account");
                                        loadingBar.setMessage("Please wait while we are checking the credentials");
                                        loadingBar.setCanceledOnTouchOutside(false);
                                        loadingBar.show();
                                        sendEmailVerification(emailV,passwordV,phonenoV);
                                        //startActivity(new Intent(Signup_form.this, Login_form.class));
                                        //Toast.makeText(Signup_form.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Signup_form.this, "Registration Failed", Toast.LENGTH_SHORT).show();

                                    }

                                    // ...
                                }
                            });
                }

            }
        });
        iamadmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register.setText("Admin Register");
                iamadmin.setVisibility(View.INVISIBLE);
                iamnotadmin.setVisibility(View.VISIBLE);
                parentName="Admins";
            }
        });
        iamnotadmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register.setText("User Register");
                iamadmin.setVisibility(View.VISIBLE);
                iamnotadmin.setVisibility(View.INVISIBLE);
                parentName="Users";
            }
        });
    }
    private void sendEmailVerification(final String email, final String password, final String phoneno)
    {
        FirebaseUser firebaseUser=firebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null)
        {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        validate(email,password,phoneno);
                        Toast.makeText(Signup_form.this, "Registration Successful, Verification mail has been sent", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        loadingBar.dismiss();
                        startActivity(new Intent(Signup_form.this, Login_form.class));


                    }
                    else
                    {
                        Toast.makeText(Signup_form.this, "Verification mail NOT been sent", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();


                    }
                }
            });
        }
    }
    private void validate(final String email, final String password, final String phoneno)
    {
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child(parentName).child(phoneno).exists()))
                {
                    HashMap<String,Object> userdataMap=new HashMap<>();
                    userdataMap.put("phoneno",phoneno);
                    userdataMap.put("email",email);
                    userdataMap.put("password",password);

                    RootRef.child(parentName).child(phoneno).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {

                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(Signup_form.this, "This user already exists", Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
