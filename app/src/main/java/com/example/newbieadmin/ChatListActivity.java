package com.example.newbieadmin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.newbieadmin.Prevalent.Prevalent;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListActivity extends AppCompatActivity {

    private RecyclerView chatlist;
    private DatabaseReference chatsRef,UsersRef;
    private String currentuserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        chatlist =(RecyclerView) findViewById(R.id.Chats_list);
        chatlist.setLayoutManager(new LinearLayoutManager(this));
        currentuserID = Prevalent.currentOnlineUser.getPhoneno();
        chatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentuserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(chatsRef,Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts,ChatsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Contacts model) {
                final String userIDs = getRef(position).getKey();
                final String[] retImage = {"default_Image"};
                UsersRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()) {

                            if (dataSnapshot.hasChild("image")) {
                                retImage[0] = dataSnapshot.child("image").getValue().toString();

                                Picasso.get().load(retImage[0]).placeholder(R.drawable.profile).into(holder.user_image);
                            }

                            final String retName = dataSnapshot.child("username").getValue().toString();
                            final String retNo = dataSnapshot.child("phoneno").getValue().toString();

                            holder.username.setText(retName);
                            holder.phoneno.setText(retNo);

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
                                    intent.putExtra("visit_user_id",userIDs);
                                    intent.putExtra("visit_user_name",retName);
                                    intent.putExtra("visit_user_image", retImage[0]);
                                    startActivity(intent);

                                }
                            });

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
                return new ChatsViewHolder(view);
            }
        };

        chatlist.setAdapter(adapter);
        adapter.startListening();

    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder{
        TextView username,phoneno;
       CircleImageView user_image;


        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.find_user_profile_name);
            phoneno = itemView.findViewById(R.id.find_user_profile_number);
            user_image = itemView.findViewById(R.id.find_user_profile_image);
        }
    }
}
