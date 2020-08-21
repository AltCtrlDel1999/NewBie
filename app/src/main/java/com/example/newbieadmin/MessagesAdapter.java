package com.example.newbieadmin;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.newbieadmin.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private List<Messages> userMessageList;
    private DatabaseReference usersRef;

    public MessagesAdapter(List<Messages> userMessageList)
    {
        this.userMessageList = userMessageList;


    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView sendermessagetext,receiverMessagetext;
        CircleImageView receiverProfileImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            sendermessagetext = (TextView)itemView.findViewById(R.id.sender_message_text);
            receiverMessagetext = (TextView)itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage = (CircleImageView)itemView.findViewById(R.id.message_profile_image);

        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_messages_layout,viewGroup,false);

        return new  MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, int i)
    {
        String messageSenderID = Prevalent.currentOnlineUser.getPhoneno();
        Messages messages = userMessageList.get(i);

        String fromuserID = messages.getFrom();
        String fromMessageType = messages.getType();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromuserID);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("image")){
                    String ReceiverImage = dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(ReceiverImage).placeholder(R.drawable.profile).into(messageViewHolder.receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(fromMessageType.equals("text")){
            messageViewHolder.receiverMessagetext.setVisibility(View.INVISIBLE);
            messageViewHolder.receiverProfileImage.setVisibility(View.INVISIBLE);
            messageViewHolder.sendermessagetext.setVisibility(View.INVISIBLE);

            if(fromuserID.equals(messageSenderID)){
                messageViewHolder.sendermessagetext.setVisibility(View.VISIBLE);
                messageViewHolder.sendermessagetext.setBackgroundResource(R.drawable.sender_messages_layout);
                messageViewHolder.sendermessagetext.setText(messages.getMessage());
            }
            else{
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.receiverMessagetext.setVisibility(View.VISIBLE);

                messageViewHolder.receiverMessagetext.setBackgroundResource(R.drawable.receiver_messages_layout);
                messageViewHolder.receiverMessagetext.setText(messages.getMessage());

            }
        }

    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }


}
