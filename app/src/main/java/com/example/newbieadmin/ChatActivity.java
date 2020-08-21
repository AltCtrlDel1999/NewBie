package com.example.newbieadmin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.newbieadmin.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private String messageReceiverID, messageReceiverName, messageReceiverImage;

    private TextView userName;
    private CircleImageView userImage;
    private ImageButton sendMessageButton;
    private EditText inputText;
    private String senderID;
    private DatabaseReference RootRef;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messagesAdapter;
    private RecyclerView usermessagesList;
    // private android.support.v7.widget.Toolbar chattoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().hide();
        sendMessageButton=findViewById(R.id.send_message_btn);
        inputText = findViewById(R.id.input_message);
        senderID = Prevalent.currentOnlineUser.getPhoneno();
        RootRef = FirebaseDatabase.getInstance().getReference();


        messageReceiverID = getIntent().getStringExtra("visit_user_id");
        messageReceiverName = getIntent().getStringExtra("visit_user_name");
        messageReceiverImage = getIntent().getStringExtra("visit_user_image");

        userName = findViewById(R.id.chat_profile_name);
        userImage = findViewById(R.id.chat_profile_image);

        // InitializeControllers();
        messagesAdapter = new MessagesAdapter(messagesList);
        usermessagesList = (RecyclerView)findViewById(R.id.private_message_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        usermessagesList.setLayoutManager(linearLayoutManager);
        usermessagesList.setAdapter(messagesAdapter);


        userName.setText(messageReceiverName);
        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.profile).into(userImage);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });

        // Toast.makeText(getApplicationContext(),"HeHe" + messageReceiverName +messageReceiverID,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        RootRef.child("Messages").child(messageReceiverID).child(senderID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Messages messages = dataSnapshot.getValue(Messages.class);

                        messagesList.add(messages);

                        messagesAdapter.notifyDataSetChanged();
                        usermessagesList.smoothScrollToPosition(usermessagesList.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void SendMessage(){
        String messageText  = inputText.getText().toString();

        if(TextUtils.isEmpty(messageText)){
            Toast.makeText(getApplicationContext(),"First Write Message",Toast.LENGTH_SHORT).show();

        }
        else{
            String messageSenderRef="Messages/" + senderID + "/" +messageReceiverID;
            String messageReceiverRef="Messages/" + messageReceiverID + "/" +senderID;

            DatabaseReference userMessageKeyRef= RootRef.child("Messages").child(senderID).child(messageReceiverID).push();

            String messagepushID = userMessageKeyRef.getKey();
            Map messageTextBody = new HashMap();
            messageTextBody.put("message",messageText);
            messageTextBody.put("type","text");
            messageTextBody.put("from",senderID);

            Map messageBodyDetails =new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagepushID,messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagepushID,messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"Message sent successfully",Toast.LENGTH_SHORT).show();


                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                    }
                    inputText.setText("");
                }
            });
        }
    }
}