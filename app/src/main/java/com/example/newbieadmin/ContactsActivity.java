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

import com.example.newbieadmin.Model.Users;
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

public class ContactsActivity extends AppCompatActivity {
    private RecyclerView MyContactsList;
    private DatabaseReference ContactsRef,UsersRef;
    private String currentuserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        MyContactsList=(RecyclerView)findViewById(R.id.contacts_list);
        MyContactsList.setLayoutManager(new LinearLayoutManager(this));
        currentuserID = Prevalent.currentOnlineUser.getPhoneno();
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentuserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ContactsRef,Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts,ContactsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts,ContactsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, final int position, @NonNull final Contacts model) {
                        String usersID = getRef(position).getKey();

                        UsersRef.child(usersID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild("image")) {
                                    String profileImage = dataSnapshot.child("image").getValue().toString();
                                    String profileName = dataSnapshot.child("username").getValue().toString();
                                    String profileno = dataSnapshot.child("phoneno").getValue().toString();
                                    holder.username.setText(profileName);
                                    holder.phoneno.setText(profileno);

                                    Picasso.get().load(profileImage).placeholder(R.drawable.profile).into(holder.user_image);
                                }
                                else{
                                    String profileName = dataSnapshot.child("username").getValue().toString();
                                    String profileno = dataSnapshot.child("phoneno").getValue().toString();
                                    holder.username.setText(profileName);
                                    holder.phoneno.setText(profileno);

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
                        ContactsViewHolder viewholder = new ContactsViewHolder(view);
                        return viewholder;
                    }
                };

        MyContactsList.setAdapter(adapter);
        adapter.startListening();

    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder
    {
        TextView username,phoneno;
        CircleImageView user_image;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.find_user_profile_name);
            phoneno = itemView.findViewById(R.id.find_user_profile_number);
            user_image = itemView.findViewById(R.id.find_user_profile_image);

        }
    }
}
