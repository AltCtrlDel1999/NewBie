package com.example.newbieadmin;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newbieadmin.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailsActivity extends AppCompatActivity {
    private String reciever_user_id,sender_user_id,current_state;

    private CircleImageView userProfileimage;
    private TextView userprofilename,userProfileno;
    private Button sendMessageRequestButton,decline_request_button;
    private DatabaseReference ChatRequestsRef,ContactsRef;

    private DatabaseReference mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        reciever_user_id=getIntent().getStringExtra("visit_user_id");
        current_state = "new";

        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestsRef = FirebaseDatabase.getInstance().getReference().child("Chat_Requests");
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        userProfileimage = findViewById(R.id.visit_profile_image);
        userprofilename = findViewById(R.id.visit_profile_name);
        userProfileno = findViewById(R.id.visit_profile_no);
        sendMessageRequestButton = findViewById(R.id.send_message_button);
        decline_request_button = findViewById(R.id.decline_message_request_button);
        Toast.makeText(getApplicationContext(),reciever_user_id,Toast.LENGTH_SHORT).show();
        sender_user_id = Prevalent.currentOnlineUser.getPhoneno();

        RetriveUserInfo();
    }

    private void RetriveUserInfo() {

        mRef.child(reciever_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.hasChild("image")){
                    String userimage = dataSnapshot.child("image").getValue().toString();
                    String username = dataSnapshot.child("username").getValue().toString();
                    String userno = dataSnapshot.child("phoneno").getValue().toString();

                    Picasso.get().load(userimage).placeholder(R.drawable.profile).into(userProfileimage);
                    userprofilename.setText(username);
                    userProfileno.setText(userno);

                    ManageChatRequests();
                }
                else{
                    String username = dataSnapshot.child("username").getValue().toString();
                    String userno = dataSnapshot.child("phoneno").getValue().toString();
                    userprofilename.setText(username);
                    userProfileno.setText(userno);

                    ManageChatRequests();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ManageChatRequests()
    {
        ChatRequestsRef.child(sender_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.hasChild(reciever_user_id))
                {
                    String request_type =dataSnapshot.child(reciever_user_id).child("request_type").getValue().toString();

                    if(request_type.equals("sent")){
                        current_state="request_sent";
                        sendMessageRequestButton.setText("Cancel Chat Request");
                    }
                    else if(request_type.equals("received"))
                    {
                        current_state = "request_received";
                        sendMessageRequestButton.setText("Accept Chat Request");



                        decline_request_button.setVisibility(View.VISIBLE);
                        decline_request_button.setEnabled(true);

                        decline_request_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CancelChatRequests();
                            }
                        });

                    }


                }
                else
                {
                        ContactsRef.child(sender_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(reciever_user_id)){
                                    current_state = "friends";
                                    sendMessageRequestButton.setText("Remove Contact");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(!sender_user_id.equals(reciever_user_id)){

            sendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessageRequestButton.setEnabled(false);

                    if(current_state.equals("new")){
                        SendChatRequests();
                    }
                    if(current_state.equals("request_sent")){
                        CancelChatRequests();
                    }
                    if(current_state.equals("request_received")){
                        AcceptChatRequest();
                    }
                    if(current_state.equals("friends")){
                        RemoveSpecificContact();
                    }
                }
            });

        }
        else {
            sendMessageRequestButton.setVisibility(View.INVISIBLE);
        }
    }

    private void RemoveSpecificContact()
    {
        ContactsRef.child(sender_user_id).child(reciever_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    ContactsRef.child(reciever_user_id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                sendMessageRequestButton.setEnabled(true);
                                current_state = "new";
                                sendMessageRequestButton.setText("Send Message");
                                decline_request_button.setVisibility(View.INVISIBLE);
                                decline_request_button.setEnabled(false);
                            }
                        }
                    });
                }

            }
        });
    }

    private void AcceptChatRequest()
    {
        ContactsRef.child(sender_user_id).child(reciever_user_id).child("Contacts").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    ContactsRef.child(reciever_user_id).child(sender_user_id).child("Contacts").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                ChatRequestsRef.child(sender_user_id).child(reciever_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        ChatRequestsRef.child(reciever_user_id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                sendMessageRequestButton.setEnabled(true);
                                                current_state = "friends";
                                                sendMessageRequestButton.setText("Remove Contact");
                                                decline_request_button.setVisibility(View.INVISIBLE);
                                                decline_request_button.setEnabled(false);
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

    }

    private void CancelChatRequests()
    {
        ChatRequestsRef.child(sender_user_id).child(reciever_user_id)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    ChatRequestsRef.child(reciever_user_id).child(sender_user_id)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                sendMessageRequestButton.setEnabled(true);
                                current_state = "new";
                                sendMessageRequestButton.setText("Send Message");
                                decline_request_button.setVisibility(View.INVISIBLE);
                                decline_request_button.setEnabled(false);
                            }
                        }
                    });
                }

            }
        });
    }

    private void SendChatRequests()
    {
        ChatRequestsRef.child(sender_user_id).child(reciever_user_id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    ChatRequestsRef.child(reciever_user_id).child(sender_user_id)
                            .child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                            sendMessageRequestButton.setEnabled(true);
                                            current_state="request_sent";
                                            sendMessageRequestButton.setText("Cancel Chat Requests");
                                }
                        }
                    });
                }
            }
        });
    }
}
