package com.example.newbieadmin;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProductDetailsActivity extends AppCompatActivity {
   // private FloatingActionButton navigation;
    private ImageView productImage;
    private TextView productname,productdescrition,productprice,navigationtv;
    String productid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
      //  navigation = findViewById(R.id.fab_navigation);
        productImage= findViewById(R.id.product_image_details);
        navigationtv= findViewById(R.id.product_navigation);
        productname = findViewById(R.id.product_name_details);
        productdescrition = findViewById(R.id.product_description_details);
        productprice = findViewById(R.id.product_price_details);
        productid = getIntent().getStringExtra("pid");

        getproductdetails(productid);

        navigationtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ProductDetailsActivity.this,MapsActivity.class);
                intent.putExtra("placelocation",productname.getText().toString());
                startActivity(intent);
            }
        });
    }

    private void getproductdetails(String productid)
    {
        DatabaseReference productref = FirebaseDatabase.getInstance().getReference().child("Products");

        productref.child(productid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Products products = dataSnapshot.getValue(Products.class);

                    productname.setText(products.getPname());
                    productdescrition.setText(products.getDescription());
                    productprice.setText(products.getPrice());
                    Picasso.get().load(products.getImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
