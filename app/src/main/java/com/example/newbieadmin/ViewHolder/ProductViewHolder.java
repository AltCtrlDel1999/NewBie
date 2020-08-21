package com.example.newbieadmin.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newbieadmin.Interface.ItemClickListner;
import com.example.newbieadmin.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
   public TextView txtProductname , TxtProductdescription,txtproductprice;
    public ImageView imageView;
    public  ItemClickListner listner;
    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = (ImageView)itemView.findViewById(R.id.product_image);
        txtProductname = (TextView) itemView.findViewById(R.id.product_name);
        TxtProductdescription = (TextView) itemView.findViewById(R.id.product_description);
        txtproductprice = (TextView)itemView.findViewById(R.id.product_price);

    }

    public void setItemClickListner(ItemClickListner listner){
        this.listner = listner;

    }

    @Override
    public void onClick(View v) {
        listner.onClick(v, getAdapterPosition(),false);

    }
}
