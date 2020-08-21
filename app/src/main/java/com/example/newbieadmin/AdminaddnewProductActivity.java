package com.example.newbieadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.newbieadmin.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class AdminaddnewProductActivity extends AppCompatActivity {
    private  String categoryName,price,description,pname,savecurrentdate,savecurrenttime;
    private ImageView inputimage;
    private EditText inputproductname,inputproductdescripition,inputprice;
    private static  final int GalleryPick =1;
    Button addroom;
    private Uri ImageUri;
    private String ProductRandomKey,downloadimageurl;
    StorageReference ProductImageRef;
    private ProgressDialog loadingbar;
    DatabaseReference Productsref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminaddnew_product);
        categoryName = getIntent().getStringExtra("category");
        ProductImageRef = FirebaseStorage.getInstance().getReference().child("Product images");
        Productsref = FirebaseDatabase.getInstance().getReference().child("Products");

        inputimage =findViewById(R.id.select_room_image);
        inputproductname =findViewById(R.id.product_name);
        inputproductdescripition =findViewById(R.id.product_description);
        inputprice = findViewById(R.id.product_price);
        addroom = findViewById(R.id.add_new_product);
        loadingbar = new ProgressDialog(this);

        inputimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        addroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });


    }

    private void OpenGallery(){
        Intent galleryintent = new Intent();
        galleryintent.setAction(Intent.ACTION_GET_CONTENT);
        galleryintent.setType("image/*");
        startActivityForResult(galleryintent,GalleryPick);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GalleryPick && resultCode==RESULT_OK && data!=null){
            ImageUri = data.getData();
            inputimage.setImageURI(ImageUri);

        }
    }

    protected void ValidateProductData(){

        price = inputprice.getText().toString();
        description = inputproductdescripition.getText().toString();
        pname = inputproductname.getText().toString();

        if(ImageUri==null){

            Toast.makeText(getApplicationContext(),"Room image is required",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(price)){
            Toast.makeText(getApplicationContext(),"Rent is required",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pname)){
            Toast.makeText(getApplicationContext(),"name is required",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(description)){
            Toast.makeText(getApplicationContext(),"description is required",Toast.LENGTH_SHORT).show();
        }
        else{
            StoreProductInformation();
        }
    }

    private void StoreProductInformation(){
        Calendar calendar = Calendar.getInstance();
        loadingbar.setTitle("Add New Product");
        loadingbar.setMessage("Please wait while we are checking room details");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM, DD, YYYY", Locale.US);
        savecurrentdate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a",Locale.US);
        savecurrenttime = currentTime.format(calendar.getTime());

        ProductRandomKey = savecurrentdate + savecurrenttime;

        final StorageReference filepath = ProductImageRef.child(ImageUri.getLastPathSegment()+ ProductRandomKey + ".jpg");

        final UploadTask uploadTask =filepath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.getMessage();
                loadingbar.dismiss();

                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(),"Image upload successfully",Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            if(task.getException()!=null) {
                                throw task.getException();
                            }
                        }

                        downloadimageurl = filepath.getDownloadUrl().toString();
                        return  filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            downloadimageurl = task.getResult().toString();
                            Toast.makeText(getApplicationContext(),"got image url successfully...",Toast.LENGTH_SHORT).show();

                            SaveProductInfotoDatabase();
                        }
                    }
                });
            }
        });
    }

    private void  SaveProductInfotoDatabase(){
        HashMap<String,Object> ProductMap = new HashMap<>();
        ProductMap.put("pid",ProductRandomKey);
        ProductMap.put("date",savecurrentdate);
        ProductMap.put("time",savecurrenttime);
        ProductMap.put("category",categoryName);
        ProductMap.put("description",description);
        ProductMap.put("price",price);
        ProductMap.put("pname",pname);
        ProductMap.put("image",downloadimageurl);
        //ProductMap.put("phone", )


        Productsref.child(ProductRandomKey).updateChildren(ProductMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                        startActivity(new Intent(getApplicationContext(), AdminaddcategoryActivity.class));
                        loadingbar.dismiss();
                        Toast.makeText(getApplicationContext(), "Product is added successfully", Toast.LENGTH_SHORT).show();

                }
                else{
                    String message="";
                    if(task.getException()!=null){
                    message=task.getException().toString();}
                    loadingbar.dismiss();

                    Toast.makeText(getApplicationContext(),"Error: "+ message,Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

}
