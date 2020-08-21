package com.example.newbieadmin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AdminaddcategoryActivity extends AppCompatActivity {

    ImageView room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminaddcategory);
        room = findViewById(R.id.room);

        room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminaddcategoryActivity.this,AdminaddnewProductActivity.class);
                intent.putExtra("category","room");
                startActivity(intent);
            }
        });

    }
}
