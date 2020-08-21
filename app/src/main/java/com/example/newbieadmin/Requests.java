package com.example.newbieadmin;

import android.content.Context;
import android.content.DialogInterface;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newbieadmin.Prevalent.Prevalent;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Requests extends AppCompatActivity {
    private RecyclerView myRequestList;
    private DatabaseReference chatRequestsRef,usersRef,ContactsRef;
    String currentuserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        chatRequestsRef= FirebaseDatabase.getInstance().getReference().child("Chat_Requests");
        usersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        ContactsRef= FirebaseDatabase.getInstance().getReference().child("Contacts");
        myRequestList=(RecyclerView)findViewById(R.id.chat_requests_list);
        myRequestList.setLayoutManager(new LinearLayoutManager(this));
        currentuserID = Prevalent.currentOnlineUser.getPhoneno();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatRequestsRef.child(currentuserID), Contacts.class).build();
        FirebaseRecyclerAdapter<Contacts, RequestsViewHolder> adapter=new FirebaseRecyclerAdapter<Contacts, RequestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestsViewHolder holder, final int position, @NonNull Contacts model) {
                holder.itemView.findViewById(R.id.request_accept_btn).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.VISIBLE);
                final String list_user_id=getRef(position).getKey();
                DatabaseReference getTypeRef=getRef(position).child("request_type").getRef();
                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            String type=dataSnapshot.getValue().toString();
                            if(type.equals("received"))
                            {
                                usersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild("image"))
                                        {
                                            String requestprofileImage = dataSnapshot.child("image").getValue().toString();

                                            Picasso.get().load(requestprofileImage).placeholder(R.drawable.profile).into(holder.user_image);
                                        }

                                        final String requestprofileName = dataSnapshot.child("username").getValue().toString();
                                        String requestprofileno = dataSnapshot.child("phoneno").getValue().toString();
                                        holder.username.setText(requestprofileName);
                                        holder.phoneno.setText(requestprofileno);
                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                CharSequence options[] = new CharSequence[]{
                                                        "Accept",
                                                        "Cancel"
                                                };
                                                AlertDialog.Builder builder = new AlertDialog.Builder(Requests.this);
                                                builder.setTitle(requestprofileName + " Chat Request");

                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which)
                                                    {
                                                        if(which == 0)
                                                        {
                                                           ContactsRef.child(currentuserID).child(list_user_id).child("Contacts").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                               @Override
                                                               public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){
                                                                        ContactsRef.child(list_user_id).child(currentuserID).child("Contacts").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                            {
                                                                                if(task.isSuccessful()){
                                                                                    chatRequestsRef.child(currentuserID).child(list_user_id)
                                                                                            .removeValue()
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    if(task.isSuccessful()){
                                                                                                        chatRequestsRef.child(list_user_id).child(currentuserID)
                                                                                                                .removeValue()
                                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                    @Override
                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                        if(task.isSuccessful()){
                                                                                                                            Toast.makeText(getApplicationContext(),"New Contact Saved",Toast.LENGTH_SHORT).show();


                                                                                                                        }
                                                                                                                    }
                                                                                                                });
                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                               }
                                                           });

                                                        }
                                                        if(which ==1)
                                                        {
                                                            chatRequestsRef.child(currentuserID).child(list_user_id)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                                chatRequestsRef.child(list_user_id).child(currentuserID)
                                                                                        .removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if(task.isSuccessful()){
                                                                                                    Toast.makeText(getApplicationContext(),"Request Removed",Toast.LENGTH_SHORT).show();


                                                                                                }
                                                                                            }
                                                                                        });
                                                                            }
                                                                        }
                                                                    });
                                                        }

                                                    }
                                                });
                                                builder.show();


                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
                RequestsViewHolder holder =new RequestsViewHolder(view);
                return holder;
            }
        };
        myRequestList.setAdapter(adapter);
        adapter.startListening();

    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder
    {
        TextView username,phoneno;
        CircleImageView user_image;
        Button acceptButton,cancelButton;
        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.find_user_profile_name);
            phoneno = itemView.findViewById(R.id.find_user_profile_number);
            user_image = itemView.findViewById(R.id.find_user_profile_image);
            acceptButton=itemView.findViewById(R.id.request_accept_btn);
            cancelButton=itemView.findViewById(R.id.request_cancel_btn);
        }
    }

}
