package com.example.bandoannhanh.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            String id = String.valueOf(product.getId());

            Log.d("PRODUCT_ID", "Product ID: " + id);

            DocumentReference cartRef = db.collection("cart").document(id);

            cartRef.get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    int oldQty = snapshot.getLong("quantity").intValue();
                    cartRef.update("quantity", oldQty + 1);
                } else {
                    cartRef.set(cartItem);
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });

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
