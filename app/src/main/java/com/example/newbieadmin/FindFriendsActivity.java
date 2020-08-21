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
import android.widget.Button;
import android.widget.TextView;

import com.example.newbieadmin.Model.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {
    private RecyclerView  FindFriendsRecyclerlist;
    private DatabaseReference usersRef;
    private Button contacts_button,chats_button,request_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        FindFriendsRecyclerlist=(RecyclerView)findViewById(R.id.FindFriendsRecycler);
        FindFriendsRecyclerlist.setLayoutManager(new LinearLayoutManager(this));
        contacts_button = findViewById(R.id.Contacts_btn);
        chats_button = findViewById(R.id.Chats_btn);
        request_button = findViewById(R.id.Requests_btn);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        contacts_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ContactsActivity.class));
            }
        });
        request_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Requests.class));
            }
        });
        chats_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ChatListActivity.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(usersRef,Users.class).build();

        FirebaseRecyclerAdapter <Users,FindFriendsViewholder> adapter =
                new FirebaseRecyclerAdapter<Users, FindFriendsViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendsViewholder holder, final int position, @NonNull Users model) {
                        holder.username.setText(model.getUsername());
                        holder.phoneno.setText(model.getPhoneno());

                        Picasso.get().load(model.getImage()).placeholder(R.drawable.profile).into(holder.user_image);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String visit_user_id =getRef(position).getKey();

                                Intent intent = new Intent(getApplicationContext(),UserDetailsActivity.class);
                                intent.putExtra("visit_user_id",visit_user_id);
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FindFriendsViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
                        FindFriendsViewholder viewholder = new FindFriendsViewholder(view);
                        return viewholder;
                    }
                };

        FindFriendsRecyclerlist.setAdapter(adapter);
        adapter.startListening();

    }

    public static class FindFriendsViewholder extends RecyclerView.ViewHolder
    {
        TextView username,phoneno;
        CircleImageView user_image;

        public FindFriendsViewholder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.find_user_profile_name);
            phoneno = itemView.findViewById(R.id.find_user_profile_number);
            user_image = itemView.findViewById(R.id.find_user_profile_image);

        }
    }
}
