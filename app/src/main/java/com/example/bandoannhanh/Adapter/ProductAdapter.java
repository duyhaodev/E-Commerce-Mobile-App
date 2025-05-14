package com.example.bandoannhanh.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bandoannhanh.Data.CartData;
import com.example.bandoannhanh.DetailPageActivity;
import com.example.bandoannhanh.Model.CartItem;
import com.example.bandoannhanh.Model.Product;
import com.example.bandoannhanh.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Context context;

    public ProductAdapter(Context context, List<Product> productList) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getName());
        holder.productQuantity.setText("Quantity: " + product.getQuantity());
        holder.productPrice.setText("$" + product.getPrice());

        int imageResId = context.getResources().getIdentifier(
                product.getImagePath().replace("drawable:", ""),
                "drawable",
                context.getPackageName()
        );
        if (imageResId != 0) {
            holder.productImage.setImageResource(imageResId);
        } else {
            holder.productImage.setImageResource(R.drawable.banana);
        }

        holder.addButton.setOnClickListener(v -> {
            CartItem cartItem = new CartItem(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    1,
                    product.getImagePath()
            );

            CartData cartData = new CartData();

            cartData.addToCart(cartItem, new CartData.OnCartOperationCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailPageActivity.class);
            intent.putExtra("product_id", product.getId());
            intent.putExtra("product_name", product.getName());
            intent.putExtra("product_price", product.getPrice());
            intent.putExtra("product_quantity", product.getQuantity());
            intent.putExtra("product_image", product.getImagePath());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productQuantity;
        ImageView productImage;
        FloatingActionButton addButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            productImage = itemView.findViewById(R.id.productImage);
            addButton = itemView.findViewById(R.id.addButton);
        }
    }
}
